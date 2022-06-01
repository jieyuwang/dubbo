/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.common.extension.factory;

import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extension.ExtensionFactory;
import com.alibaba.dubbo.common.extension.ExtensionLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 自适应的AdaptiveExtensionLoader
 * AdaptiveExtensionFactory
 * Dubbo会为每一个扩展创建一个自适应实例。
 * 如果扩展类上有@Adaptive，会使用该类作为自适应类。
 * 如果没有，Dubbo会为我们创建一个。
 * 所以`ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension())`会返回一个AdaptiveExtensionLoader实例，作为自适应扩展实例。
 * AdaptiveExtensionLoader会遍历所有的ExtensionFactory实现，尝试着去加载扩展。
 * 如果找到了，返回。如果没有，在下一个ExtensionFactory中继续找。
 *
 * Dubbo内置了两个ExtensionFactory，分别从Dubbo自身的扩展机制和Spring容器中去寻找。
 * 由于ExtensionFactory本身也是一个扩展点，我们可以实现自己的ExtensionFactory，让Dubbo的自动装配支持我们自定义的组件。
 */
@Adaptive
public class AdaptiveExtensionFactory implements ExtensionFactory {

    //用于缓存所有的工厂实现，包括SpiExtensionFactory和SpringExtensionFactory。
    private final List<ExtensionFactory> factories;

    public AdaptiveExtensionFactory() {
        //ExtensionFactory也加了SPI注解，获取工厂的所有的扩展点加载器
        ExtensionLoader<ExtensionFactory> loader = ExtensionLoader.getExtensionLoader(ExtensionFactory.class);
        List<ExtensionFactory> list = new ArrayList<ExtensionFactory>();
        //遍历所有的工厂名称，获取对应的工厂，缓存
        for (String name : loader.getSupportedExtensions()) {
            list.add(loader.getExtension(name));
        }
        factories = Collections.unmodifiableList(list);
    }

    @Override
    public <T> T getExtension(Class<T> type, String name) {
        //被AdaptiveExtensionFactory缓存的工厂会通过TreeSet进行排序，SPI排在前面，Spring排在后面
        for (ExtensionFactory factory : factories) {
            //当调用getExtension方法时，会遍历所有的工厂，先从SPI容器中获取扩展类，如果没找到，则再从Spring容器中查找。
            T extension = factory.getExtension(type, name);
            if (extension != null) {
                return extension;
            }
        }
        return null;
    }

}
