/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shadowell.doraemon.core.algo.array;

import java.util.HashMap;
import java.util.Map;

// 数组去重
public class ArrayMain {

	public static void main(String[] args) {
		int[] a =  {1,2,3,3,4,4,4,6,7,8,8};
		drop(a);

		String str="abc**gh*58*r";
		char[] ch=str.toCharArray();
		moveChar(ch,str.length());
		System.out.println(ch);
	}

	public static void drop(int[] arr) {
		int[] result = new int[arr.length];
		int index = 0;
		int sum = 0;
		outer: for (int i = 0; i< arr.length; i++) {
			for (int j = i+1; j<arr.length; j++) {
				if (arr[i] == arr[j]) {
					sum++;
					continue outer;
				}
			}
			result[index] = arr[i];
			index++;
		}
		for (int k = 0; k < result.length - sum; k++) {
			System.out.print(result[k]);
		}
	}

	public static void orderFormat(char[] arr) {
		char[] result = new char[arr.length];
		char[] charRes = new char[arr.length];
		for (int i = 0; i<arr.length; i++) {
			int tmp = 0;
			if ('*' == arr[i]) {
				result[i - tmp + 1] = arr[i];
			} else {
				tmp += 1;
				charRes[i] = arr[i];
			}
		}
	}

	public static void moveChar(char[] ch,int len) {
		int point=len-1;
		int let=len-1;
		Map<String, Integer> map = new HashMap<>();

		while(point!=0&&let!=0)
		{
			while(ch[point]!='*'&&point!=0)  //first * from the end
			{
				point--;
			}
			if(point==0)             //all ch are *
				return;
			let=point;
			while(ch[let]=='*'&&let!=0)   //the first letter before *
			{
				let--;
			}
			while(ch[let]!='*'&&ch[point]=='*')
			{
				char tem=ch[let];
				ch[let]=ch[point];
				ch[point]=tem;
				if(point!=0)
					point--;
				if(let!=0)
					let--;
			}
		}
	}
}
