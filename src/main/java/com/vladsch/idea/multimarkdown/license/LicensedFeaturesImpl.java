/*
 * Copyright (c) 2015-2015 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.idea.multimarkdown.license;

import com.intellij.util.ReflectionUtil;
import com.vladsch.idea.multimarkdown.MultiMarkdownPlugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LicensedFeaturesImpl {
    final protected Class forClass;
    final protected Map<String, Integer> licensedFeaturesMap = new HashMap<String, Integer>();
    final protected Map<String, Method> unlicensedValuesMap = new HashMap<String, Method>();

    public LicensedFeaturesImpl(Class forClass) {
        this.forClass = forClass;

        // load the methods labeled with annotations
        // for now no method overloading is checked, all licensed annotations for a method name form a combined set of licensing
        // that these support. Since only Feature.ALL will be used for now this is not a problem. Really need to create
        // a compile time processor that will wrap the function and do all processing at compile time with no runtime overhead
        List<Method> methods = ReflectionUtil.getClassDeclaredMethods(forClass);
        Map<String, ArrayList<Method>> methodMap = new HashMap<String, ArrayList<Method>>();

        for (Method method : methods) {
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (final Annotation annotation : annotations) {
                String methodName = method.getName();
                boolean addMethod = false;
                if (annotation instanceof LicensedFeatures) {
                    addMethod = true;
                    LicensedFeatures licensedFeatures = (LicensedFeatures) annotation;

                    int typeFlags = licenseTypeFlags(licensedFeatures.value());
                    if (!licensedFeaturesMap.containsKey(methodName)) {
                        licensedFeaturesMap.put(methodName, typeFlags);
                    } else {
                        licensedFeaturesMap.put(methodName, licensedFeaturesMap.get(methodName) | typeFlags);
                    }
                } else if (annotation instanceof LicensedFeature) {
                    addMethod = true;
                    LicensedFeature licensedFeature = (LicensedFeature) annotation;
                    int typeFlags = licenseTypeFlags(licensedFeature);
                    if (!licensedFeaturesMap.containsKey(methodName)) {
                        licensedFeaturesMap.put(methodName, typeFlags);
                    } else {
                        licensedFeaturesMap.put(methodName, licensedFeaturesMap.get(methodName) | typeFlags);
                    }
                } else if (annotation instanceof UnlicensedValue) {
                    addMethod = true;
                    String forMethod = ((UnlicensedValue) annotation).method();
                    assert !unlicensedValuesMap.containsKey(forMethod) : methodName + "annotated with @UnlicensedValue for " + forMethod + " is already defined by " + unlicensedValuesMap.get(forMethod);

                    // these should take no parameters and have a return type that is not void
                    assert method.getParameterTypes().length == 0 : methodName + "annotated with @UnlicensedValue for " + forMethod + " should take no parameters ";

                    unlicensedValuesMap.put(forMethod, method);
                }

                if (addMethod) {
                    if (!methodMap.containsKey(methodName)) {
                        methodMap.put(methodName, new ArrayList<Method>(1));
                    }
                    methodMap.get(methodName).add(method);
                }
            }
        }

        // validate that all forMethods are contained in licensedMethods and that they all return compatible types with functions that they shadow
        for (String forMethodName : unlicensedValuesMap.keySet()) {
            Method unlicensedValue = unlicensedValuesMap.get(forMethodName);
            assert licensedFeaturesMap.containsKey(forMethodName) : "method " + forMethodName + " not annotated with @LicensedFeature, but is target for @UnlicensedValue " + unlicensedValue.getName();
            ArrayList<Method> forMethods = methodMap.get(forMethodName);

            for (Method forMethod : forMethods) {
                assert unlicensedValue.getReturnType().isAssignableFrom(forMethod.getReturnType()) : unlicensedValue.getName() + " @UnlicensedValue for " + forMethodName + " has incompatible return type";
            }
        }
    }

    protected int licenseTypeFlags(LicensedFeature... licensedFeatures) {
        int flags = 0;
        for (LicensedFeature licensedFeature : licensedFeatures) {
            flags |= licensedFeature.type().getLicenseFlags();
        }
        return flags;
    }

    public boolean isLicensed() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String methodName = stackTraceElements[1].getMethodName();
        assert licensedFeaturesMap.containsKey(methodName) : "isLicensed called from method " + methodName + " not annotated with @Licensed";
        return MultiMarkdownPlugin.areAllLicensed(licensedFeaturesMap.get(methodName));
    }

    // this really needs to be implemented in a processor so that code is generated according to types
    Object unlicensedValue(Object thizz) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String methodName = stackTraceElements[1].getMethodName();
        assert unlicensedValuesMap.containsKey(methodName) : "unlicensedValue called from method " + methodName + " which does not an associated @UnlicensedValue function";
        Method unlicensedValue = unlicensedValuesMap.get(methodName);

        Object result = null;
        try {
            result = unlicensedValue.invoke(thizz);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return result;
    }
}
