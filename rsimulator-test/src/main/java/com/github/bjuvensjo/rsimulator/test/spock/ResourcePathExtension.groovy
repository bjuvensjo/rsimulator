package com.github.bjuvensjo.rsimulator.test.spock

import groovy.transform.CompileStatic
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.SpecInfo


@CompileStatic
class ResourcePathExtension implements IAnnotationDrivenExtension<ResourcePath> {
    private ResourcePathInterceptor resourcePathInterceptor

    @Override
    void visitFieldAnnotation(ResourcePath annotation, FieldInfo fieldInfo) {
        if (resourcePathInterceptor) {
            throw new IllegalStateException('Only one field can have annotation ' + annotation.annotationType().simpleName)
        }
        resourcePathInterceptor = new ResourcePathInterceptor(fieldInfo: fieldInfo, regex: annotation.fixtureDelimiter(), simple: annotation.simpleClassName(), rootOnly: annotation.rootOnly())
    }

    @Override
    void visitSpec(SpecInfo spec) {
        spec.addSetupInterceptor(resourcePathInterceptor)
    }

    private static class ResourcePathInterceptor implements IMethodInterceptor {
        FieldInfo fieldInfo
        String regex
        boolean simple
        boolean rootOnly

        String getResourcePath(Class specClass, String featureName) {
            String root = specClass.protectionDomain.codeSource.location.path
            if (root.contains(':') && root.startsWith('/')){
                root = root.substring(1)
            }
            if (rootOnly) {
                return root
            }
            String classNamePart = simple ? specClass.simpleName : specClass.name.replaceAll(/\./, '/')
            String methodNamePart = featureName.split(regex)[0].trim()
            return root + classNamePart + '/' + methodNamePart + '/'
        }

        @Override
        void intercept(IMethodInvocation invocation) throws Throwable {
            String value = getResourcePath(invocation.instance.class, invocation.feature.name)
            fieldInfo.writeValue(invocation.instance, value)
            invocation.proceed()
        }
    }
}
