// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.translator;

import com.vladsch.boxed.json.BoxedJsArray;
import com.vladsch.boxed.json.BoxedJsValue;
import com.vladsch.boxed.json.BoxedJson;

import javax.json.JsonArray;
import java.util.ArrayList;
import java.util.List;

public class TranslateUtil {
    public static List<String> asStringList(JsonArray jsonArray) {
        final BoxedJsArray jsArray = BoxedJson.of(jsonArray);
        int iMax = jsArray.size();

        ArrayList<String> list = new ArrayList<>(iMax);
        for (int i = 0; i < iMax; i++) {
            final BoxedJsValue jsValue = jsArray.get(i);
            if (jsValue.isString()) {
                list.add(jsValue.asJsString().getString());
            } else {
                // the rest we convert to json value string
                list.add(jsValue.toString());
            }
        }
        return list;
    }
}
