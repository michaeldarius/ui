/*******************************************************************************
 * Copyright 2011 Universidad Polit�cnica de Madrid
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
package org.universAAL.ui.proof;

import java.io.IOException;

import org.universAAL.ui.full.test.ISubscriber;

public class ScanTest {

	public static void main(String[] args) {
		try {
			String[] a = ISubscriber.getClassNamesFromPackage("org.universAAL.ui.full.test.forms");
			for (int i=0; i< a.length; i++)
				System.out.println(a[i]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}