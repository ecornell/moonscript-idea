/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eightbitmage.moonscript.lang.psi.types;

public final class MoonType {
    public static final MoonType BOOLEAN = new MoonType("BOOLEAN");
    public static final MoonType NUMBER = new MoonType("NUMBER");
    public static final MoonType STRING = new MoonType("STRING");
    public static final MoonType TABLE = new MoonType("TABLE");
    public static final MoonType USERDATA = new MoonType("USERDATA");
    public static final MoonType LIGHTUSERDATA = new MoonType("LIGHTUSERDATA");
    public static final MoonType NIL = new MoonType("NIL");
    public static final MoonType THREAD = new MoonType("THREAD");
    public static final MoonType ANY = new MoonType("ANY");

    private String name;
    private MoonType(String name){ this.name = name; }

    @Override
    public String toString() {
        return name;   
    }
}
