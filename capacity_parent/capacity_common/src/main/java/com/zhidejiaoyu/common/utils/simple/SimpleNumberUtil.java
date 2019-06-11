package com.zhidejiaoyu.common.utils.simple;

/**
 * 随机例句, 去重随机数
 *
 * @author qizhentao
 * @version 1.0
 */
public class SimpleNumberUtil {

	/**
	 * 生成打乱的例句
	 *
	 * @param correct 根据空格拆分的例句
	 * @return
	 */
	public static String[] randomWord(String[] correct) {
		// 打乱顺序题
		String[] chaoc = new String[correct.length];
		int[] is = SimpleNumberUtil.random(correct.length);
		for (int i = 0; i < is.length; i++) {
			int j = is[i];
			chaoc[i] = correct[j];
		}
		return chaoc;
	}

	/**
	 * 去重随机数
	 *
	 * @param size 随机数长度,生成几位随机数
	 * @return
	 */
	public static int[] random(int size) {

		  int Random[] = new int[size];
		  for(int i = 0 ; i < size ; i++)
		  {
		  // int ran=-1;
		   while(true)
		   {
		    int ran = (int)(size*Math.random());
		    for(int j = 0 ; j < i ; j++)
		    {
		     if(Random[j] == ran)
		     {
		      ran = -1;
		      break;
		     }
		    }
		    if(ran != -1)
		    {
		     Random[i] = ran;
		     break;
		    }

		   }

		  }
		  return Random;
		 // for(int i = 0 ; i < size ; i ++)
		 // {
		  // System.out.print(Random[i]+",");
		 // }
	}
}
