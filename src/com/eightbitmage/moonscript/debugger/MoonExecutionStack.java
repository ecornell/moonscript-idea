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

import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jon S Akhtar
 * Date: 4/28/11
 * Time: 11:06 AM
 */
public class MoonExecutionStack extends XExecutionStack {

    private MoonDebuggerController myController;
    MoonStackFrame myTopFrame;
    String myEncodedStackFrame = null;
    Project myProject;

    public MoonExecutionStack(Project project, MoonDebuggerController myController, String displayName, MoonStackFrame
            topFrame, String stack) {
        super(displayName);
        this.myController = myController;

        myTopFrame = topFrame;
        myEncodedStackFrame = stack;
        myProject = project;
    }


    @Override
    public XStackFrame getTopFrame() {
        return myTopFrame;
    }

    @Override
    public void computeStackFrames(int firstFrameIndex, XStackFrameContainer container) {
        String[] frames = myEncodedStackFrame.split("#");


        List<MoonStackFrame> frameList = new ArrayList<MoonStackFrame>();

        int reverseIndex = frames.length - 1 - firstFrameIndex;
        if (frames.length > 0) {
            for (int i = reverseIndex; i > 0; i--) {
                String[] frameData = frames[i].split("[|]");

                MoonPosition position = new MoonPosition(frameData[1], Integer.parseInt(frameData[2]));

                MoonStackFrame frame = new MoonStackFrame(myProject, myController, MoonPositionConverter.createLocalPosition(position));

                frameList.add(frame);
            }

            container.addStackFrames(frameList, true);
        }
    }
}
