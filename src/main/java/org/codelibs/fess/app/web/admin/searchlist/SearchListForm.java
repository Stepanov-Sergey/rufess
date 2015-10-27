/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.codelibs.fess.app.web.admin.searchlist;

import java.io.Serializable;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class SearchListForm implements Serializable {

    private static final long serialVersionUID = 1L;

    //@Maxbytelength(maxbytelength = 1000)
    public String query;

    //@Digits(integer=10, fraction=0)
    public String start;

    //@Digits(integer=10, fraction=0)
    public String pn;

    //@Digits(integer=10, fraction=0)
    public String num;

    //@Required(target = "confirmDelete,delete")
    public String docId;

    //@Required(target = "confirmDelete")
    public String url;
}
