package com.rs.rslib.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

/**
 * android资源访问工具类
 */
public class ResourcesUtil {
	public  enum ResourceType{
		MIPMAP("mipmap"),
		DRAWABLE("drawable"),
		STRING("string"),
		STRINGARRAY("array");

		String value;
		ResourceType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
	/**
	 * 根据资源名称获取资源的id
	 * @param context
	 * @param idName
	 * @param resourceType drawable mipmap  layout string array
	 */
	public static int getResourcesByIdentifier(Context context,
			String idName, String resourceType) {
		try {
			if (context == null || TextUtils.isEmpty(idName)
                    || TextUtils.isEmpty(resourceType)) {
                return 0;
            }

			return context.getResources().getIdentifier(idName, resourceType,
                    context.getPackageName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int getDimensionPixelOffset(Context context,int dimenResId) {
		return getResources(context).getDimensionPixelOffset(dimenResId);
	}

	private static Resources getResources(Context context) {
		return context.getResources();
	}

}
