/*
 * Copyright 2011 Jon S Akhtar (Sylvanaar)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.eightbitmage.moonscript.debugger;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.impl.XSourcePositionImpl;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/3/11
 * Time: 12:21 PM
 */
public class MoonPositionConverter {

    public static MoonPosition createRemotePosition(XSourcePosition xSourcePosition)
    {
        MoonPosition pos;

        assert xSourcePosition != null;

        pos = new MoonPosition(xSourcePosition.getFile().getPath(), xSourcePosition.getLine() + 1);

        return pos;
    }

    public static XSourcePosition createLocalPosition(MoonPosition moonPosition)
    {
        assert moonPosition != null;

        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(moonPosition.getPath());

        return XSourcePositionImpl.create(file, moonPosition.getLine() - 1);
    }
}
