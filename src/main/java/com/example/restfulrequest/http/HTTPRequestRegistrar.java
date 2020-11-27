package com.example.restfulrequest.http;

import com.example.restfulrequest.annotation.HTTPRequest;
import com.example.restfulrequest.annotation.HTTPUtil;
import com.example.restfulrequest.http.handler.DemoHttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * 所有实现了ImportBeanDefinitionRegistrar的类的都会被ConfigurationClassPostProcessor处理
 */
@Slf4j
public class HTTPRequestRegistrar implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware, BeanFactoryAware {

    private ClassLoader classLoader;
    private ResourceLoader resourceLoader;
    private Environment environment;
    private BeanFactory beanFactory;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        registerHttpRequest(beanDefinitionRegistry);
    }

    /**
     * 利用ClassPathScanningCandidateComponentProvider获取标注了HTTPUtil注解的接口，并使用JDK动态代理为期生成代理对象。
     * 然后使用DefaultListableBeanFactory将代理对象注册到容器中
     */
    private void registerHttpRequest(BeanDefinitionRegistry beanDefinitionRegistry) {
        // 类扫描器
        ClassPathScanningCandidateComponentProvider classScanner = getClassScanner();
        classScanner.setResourceLoader(this.resourceLoader);
        // 只找 @HTTPUtil注解 的接口
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(HTTPUtil.class);
        classScanner.addIncludeFilter(annotationTypeFilter);
        // 扫描指定的package，得到定义的bean，注册bean
        String basePack = "com.example.restfulrequest";
        Set<BeanDefinition> beanDefinitionSet = classScanner.findCandidateComponents(basePack);
        for (BeanDefinition beanDefinition : beanDefinitionSet) {
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                registerBeans(((AnnotatedBeanDefinition) beanDefinition));
            }
        }
    }

    /**
     * 创建动态代理，并动态注册到容器中
     */
    private void registerBeans(AnnotatedBeanDefinition annotatedBeanDefinition) {
        String className = annotatedBeanDefinition.getBeanClassName();
        // 注册bean
        ((DefaultListableBeanFactory) this.beanFactory).registerSingleton(className, createProxy(annotatedBeanDefinition));
    }

    /**
     * 构造Class扫描器，只扫描顶级接口，不扫描内部类
     */
    private ClassPathScanningCandidateComponentProvider getClassScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {

            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                // 是否接口
                if (beanDefinition.getMetadata().isInterface()) {
                    try {
                        // 是否注解
                        Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(), classLoader);
                        return !target.isAnnotation();
                    } catch (Exception ex) {
                        log.error("load class exception:", ex);
                    }
                }
                return false;
            }
        };
    }

    /**
     * 创建JDK动态代理
     */
    private Object createProxy(AnnotatedBeanDefinition annotatedBeanDefinition) {
        try {
            AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
            // 被代理接口
            Class<?> target = Class.forName(annotationMetadata.getClassName());
            // 代理方法
            InvocationHandler invocationHandler = createInvocationHandler();
            Object proxy = Proxy.newProxyInstance(HTTPRequest.class.getClassLoader(), new Class[]{target}, invocationHandler);
            return proxy;
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 创建InvocationHandler，将方法调用全部代理给DemoHttpHandler
     */
    private InvocationHandler createInvocationHandler() {
        return new InvocationHandler() {
            private DemoHttpHandler demoHttpHandler = new DemoHttpHandler();

            /**
             *
             * @param proxy 代理对象
             * @param method 被代理方法
             * @param args 被代理方法的参数
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                return demoHttpHandler.handle(method);
            }
        };
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}