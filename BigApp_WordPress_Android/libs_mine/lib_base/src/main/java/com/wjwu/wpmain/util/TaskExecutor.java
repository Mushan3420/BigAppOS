package com.wjwu.wpmain.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wjwu on 2015/8/28.
 */
public class TaskExecutor {
	private static final ExecutorService executorService = Executors
			.newFixedThreadPool(6);;

	/** * 私有的默认构造子 */
	private TaskExecutor() {
	}

	/**
	 * * 静态工厂方法
	 */
	public static void execute(Runnable task) {
		executorService.submit(task);
	}
}
