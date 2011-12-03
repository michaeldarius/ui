/*******************************************************************************
 * Copyright 2011 Universidad Politécnica de Madrid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.universAAL.ui.handler.newGui.model.FormControl;

import javax.swing.JComponent;

import org.universAAL.middleware.io.rdf.Output;
import org.universAAL.ui.handler.newGui.model.Model;

/**
 *
 * @author <a href="mailto:amedrano@lst.tfo.upm.es>amedrano</a>
 * @see Output
 */
public abstract class OutputModel extends Model {

    public OutputModel(Output control) {
        super(control);
    }

    final public boolean isValid(JComponent component) {
        // All outputs are all ways valid!
        return true;
    }

}