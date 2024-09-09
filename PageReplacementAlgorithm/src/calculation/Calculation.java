package calculation;

public class Calculation {
	// accuracy[0]、accuracy[1]、accuracy[2]分别存储不同页框数下三种页面置换算法的命中率
	// accuracy[3]存储FIFO与LRU算法的命中率大小对比结果
	public static double[][] accuracy = new double[4][37];
	// 存储页框里的页地址信息
	public static int[][] pageCapacity;


	//OPT算法
	public static void calculateOPT(int[] pageAddresses, int pageFrame) {
		// pageCapacity[pageFrame][0]放置页地址，pageCapacity[pageFrame][1]表示该页地址在未来是否出现（“是”用1表示，否则置0）
		pageCapacity = new int[pageFrame][2];
		int currentPageFrame = 0;// 当前页框数
		// 用于检查用户内存容量是否有余
		for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
			pageCapacity[currentPageFrame][0] = -1;
		}
		double pageFault = 0;// 缺页次数
		
		for(int k = 0; k < 400; k ++) {
			// 判断是否已存在该页地址
			for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
				if(pageCapacity[currentPageFrame][0] == pageAddresses[k]) {
					break;
				}
			}
			// 处理需插入新页地址的情况
			if(currentPageFrame == pageFrame) {
				// 当有剩余用户内存容量时
				for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
					if(pageCapacity[currentPageFrame][0] == -1) {
						pageCapacity[currentPageFrame][0] = pageAddresses[k];
						break;
					}
				}
				// 当需要置换页地址时
				if(currentPageFrame == pageFrame) {
					int replacement = 0;
					// 检查pageAddresses[k]之后是否出现页地址与现有页框内容重复
					for(int i = k + 1, flag = 0; i < 400; i ++) {
						for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
							if(pageCapacity[currentPageFrame][0] == pageAddresses[i]) {
								if(pageCapacity[currentPageFrame][1] == 0) {
									pageCapacity[currentPageFrame][1] = 1;// 标记该处与未来要访问的页地址重复
									flag ++;
								}
								break;
							}
						}
						if(flag == pageFrame - 1) {// 如果出现pageFrame-1次重复，则置换剩下那一个未出现重复的页地址
							break;
						}
					}
					// 找到页框中在未来不出现重复的第一个页地址
					for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
						if(pageCapacity[currentPageFrame][1] == 0) {
							replacement = currentPageFrame;
							break;
						}
					}
					pageCapacity[replacement][0] = pageAddresses[k];
					pageFault ++;// 增加缺页中断次数
					// 清楚检查痕迹
					for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
						pageCapacity[currentPageFrame][1] = 0;
					}
				}
			}
		}
		
		accuracy[0][pageFrame - 4] =  1 - pageFault / 400;
		pageFault = 0;// 清空数据
	}

	//FIFO算法
	public static void calculateFIFO(int[] pageAddresses, int pageFrame) {
		// pageCapacity[pageFrame][0]放置页地址，pageCapacity[pageFrame][1]表示该页地址滞留在该页框的时长
		pageCapacity = new int[pageFrame][2];
		int currentPageFrame = 0;// 当前页框数
		// 用于检查用户内存容量是否有余
		for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
			pageCapacity[currentPageFrame][0] = -1;
		}
		double pageFault = 0;// 缺页次数
		
		for(int k = 0; k < 400; k ++) {
			// 判断是否已存在该页地址
			for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
				if(pageCapacity[currentPageFrame][0] == pageAddresses[k]) {
					// 增加页框中有效页地址的滞留时长
					for(int i = 0; i < pageFrame; i ++) {
						if(pageCapacity[i][0] != -1) {
							pageCapacity[i][1] ++;
						}
					}
					break;
				}
			}
			// 处理需插入新页地址的情况
			if(currentPageFrame == pageFrame) {
				// 当有剩余用户内存容量时
				for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
					if(pageCapacity[currentPageFrame][0] == -1) {
						pageCapacity[currentPageFrame][0] = pageAddresses[k];
						// 增加页框中有效页地址的滞留时长，当前页地址的滞留时长置为1
						for(int i = 0; i < pageFrame; i ++) {
							if(pageCapacity[i][0] != -1) {
								pageCapacity[i][1] ++;
							}
						}
						pageCapacity[currentPageFrame][1] = 1;
						break;
					}
				}
				// 当需要置换页地址时
				if(currentPageFrame == pageFrame) {
					int replacement = 0;
					// 比较选出当前滞留时长最大的页地址，出现相同情况则按顺序选取
					for(currentPageFrame = 1; currentPageFrame < pageFrame; currentPageFrame ++) {
						if(pageCapacity[currentPageFrame][1] > pageCapacity[replacement][1]) {
							replacement = currentPageFrame;
						}
					}
					pageCapacity[replacement][0] = pageAddresses[k];
					// 增加页框中各页地址的滞留时长，当前页地址的滞留时长置为1
					for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
							pageCapacity[currentPageFrame][1] ++;
					}
					pageCapacity[replacement][1] = 1;
					pageFault ++;// 增加缺页中断次数
				}
			}
		}
		
		accuracy[1][pageFrame - 4] =  1 - pageFault / 400;
		pageFault = 0;// 清空数据
	}

	//LRU算法
	public static void calculateLRU(int[] pageAddresses, int pageFrame) {
		// pageCapacity[pageFrame][0]放置页地址，pageCapacity[pageFrame][1]表示该页地址的最新被访问时间值
		pageCapacity = new int[pageFrame][2];
		int currentPageFrame = 0;// 当前页框数
		// 用于检查用户内存容量是否有余
		for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
			pageCapacity[currentPageFrame][0] = -1;
		}
		double pageFault = 0;// 缺页次数
		
		for(int k = 0; k < 400; k ++) {
			// 判断是否已存在该页地址
			for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
				if(pageCapacity[currentPageFrame][0] == pageAddresses[k]) {
					pageCapacity[currentPageFrame][1] = 0;
					break;
				}
			}
			// 处理需插入新页地址的情况
			if(currentPageFrame == pageFrame) {
				// 当有剩余用户内存容量时
				for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
					if(pageCapacity[currentPageFrame][0] == -1) {
						pageCapacity[currentPageFrame][0] = pageAddresses[k];
						// 增加页框中有效页地址的最新被访问时间值，当前页地址的最新被访问时间值置为0
						for(int i = 0; i < pageFrame; i ++) {
							if(pageCapacity[i][0] != -1) {
								pageCapacity[i][1] ++;
							}
						}
						pageCapacity[currentPageFrame][1] = 0;
						break;
					}
				}
				// 当需要置换页地址时
				if(currentPageFrame == pageFrame) {
					int replacement = 0;
					// 比较选出当前最新被访问时间值最大的页地址，出现相同情况则按顺序选取
					for(currentPageFrame = 1; currentPageFrame < pageFrame; currentPageFrame ++) {
						if(pageCapacity[currentPageFrame][1] > pageCapacity[replacement][1]) {
							replacement = currentPageFrame;
						}
					}
					pageCapacity[replacement][0] = pageAddresses[k];
					// 增加页框中有效页地址的最新被访问时间值，当前页地址的最新被访问时间值置为0
					for(currentPageFrame = 0; currentPageFrame < pageFrame; currentPageFrame ++) {
						pageCapacity[currentPageFrame][1] ++;
					}
					pageCapacity[replacement][1] = 0;
					pageFault ++;// 增加缺页中断次数
				}
			}
		}
		
		accuracy[2][pageFrame - 4] =  1 - pageFault / 400;
		pageFault = 0;// 清空数据
	}


	// 输出命中率情况以及比较结果
	/*public static void output() {
		System.out.println("----------------------------------------------");
		System.out.println("页框数    OPT命中率    FIFO命中率    LRU命中率");
		for(int j = 0, k = 4; k <= 40; j ++, k ++) {
			System.out.printf("%-10d%-13.4f%-14.4f%-9.4f", k, accuracy[0][j], accuracy[1][j], accuracy[2][j]);// 格式化输出
			System.out.println();
		}
		System.out.println();
		System.out.println("FIFO与LRU的命中率大小对比结果：");
		// 计算对比结果，用accuracy[3][0]、accuracy[3][1]和accuracy[3][2]记录三种对比结果出现次数的统计
		for(int j = 0; j < 37; j ++) {
			if(accuracy[1][j] > accuracy[2][j]) {
				accuracy[3][0] ++;
			}else if(accuracy[1][j] == accuracy[2][j]) {
				accuracy[3][1] ++;
			}else {
				accuracy[3][2] ++;
			}
		}
		System.out.println("FIFO > LRU : "+ (int)accuracy[3][0] + "次");
		System.out.println("FIFO = LRU : "+ (int)accuracy[3][1] + "次");
		System.out.println("FIFO < LRU : "+ (int)accuracy[3][2] + "次");
		System.out.println("----------------------------------------------");
		
		accuracy = new double[4][37];// 清空数据
	}*/
}
