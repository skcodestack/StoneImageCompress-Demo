/***
 *********************************************************** 
 ******************** 凯子出品，必属精品**********************
 *********************************************************** 
 * 创建者：Root 2015
 *********************************************************** 
 * 创建于： 2015-12-16 上午10:50:01
 *********************************************************** 
 ******************** 凯子出品，必属精品**********************
 *********************************************************** 
 ***/
package github.com.stoneimagecompress.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 * @author root
 *
 */
public class ThreadPoolManager {
	/**
	 * 线程池管理
	 * JDK1.5 之前不可以使用DCL(双锁检测)来实现单例
	 */
	private  static volatile ThreadPoolManager manager=null;
	/**
	 * 耗时线程池
	 */
	private  ThreadPool  longthread=null;
	/**
	 * 不耗时线程池
	 */
	private  ThreadPool  shortthread=null;
	/**
	 * 单列模型
	 * @return
	 */
	private ThreadPoolManager(){
		
	}
	/**
	 * 获取对象
	 * (双锁检测)来实现单例, JDK 1.5前 ，不可使用
	 * @return
	 */
	public  static   ThreadPoolManager  getInstance()
	{
		if(manager==null)
		{
			synchronized (ThreadPool.class) {
				if(manager==null) {
					manager = new ThreadPoolManager();
				}
			}
		}
		return  manager;
	}
	/**
	 * 耗时操作线程池
	 * @return
	 */
	public synchronized ThreadPool getLongTreadPool()
	{
		if(longthread==null)
			longthread=new ThreadPool(3, 3, 1);
		return  longthread;
	}
	/**
	 * 不耗时操作线程池
	 * @return
	 */
	public synchronized  ThreadPool getShortTreadPool()
	{
		if(shortthread==null)
			shortthread=new ThreadPool(3, 3, 1);
		return  shortthread;
	}
	
	/**
	 * 线程池
	 * @author Administrator
	 *
	 */
	public static class  ThreadPool
	{ 
		ThreadPoolExecutor executor;
		int maxlink=0;
		int bylink=0;
		int time_live=2;
		public  ThreadPool(int maxlink,int bylink,int time_live)
		{
			this.maxlink=maxlink;
			this.bylink=bylink;
			this.time_live=time_live;
			executor = new ThreadPoolExecutor(maxlink, bylink, time_live, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(10));
		}
		/**
		 * 线程池执行   ThreadPoolExecutor
		 * 				1）newFixedThreadPool 和 newSingleThreadExecutor:
		 * 				主要问题是堆积的请求处理队列可能会耗费非常大的内存，甚至 OOM。
		 * 				2）newCachedThreadPool 和 newScheduledThreadPool:
		 * 				主要问题是线程数最大数是 Integer.MAX_VALUE，可能会创建数量非常多的线程，甚至 OOM
		 * @param runable
		 */
		public  void  excute(Runnable runable){

			executor.execute(runable);
		}
		/**
		 * 线程池取消
		 * @param runable
		 */
		public  void  cancel(Runnable runable)
		{
			if(executor!=null && !executor.isShutdown() && !executor.isTerminated())
			{
				executor.remove(runable);
			}
		}
	}


}
