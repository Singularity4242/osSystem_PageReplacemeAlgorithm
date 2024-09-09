package produceData;

public class RandomNumber {
	// 存储指令地址
	static int[] insAddresses;
	
	// 随机生成400条指令地址
	public static int[] produce() {
		// 存储随机生成的400条指令地址
		insAddresses = new int[400];
		
		// 分区间采用非顺序与顺序方法轮流产生随机指令地址
		int k = 0;// 各区间增幅
		while(k < 200) {
			// [0，199]生成随机指令地址
			insAddresses[0 + k] = (int) (Math.random() * 200);
			if(insAddresses[0 + k] != 199) {// 保证顺序执行后不跨区间
				insAddresses[0 + k + 1] = insAddresses[0 + k] + 1;
			}else {
				insAddresses[0 + k + 1] = insAddresses[0 + k];
			}
			// [200，399]生成随机指令地址
			insAddresses[200 + k] = (int) (Math.random() * 200) + 200;
			if(insAddresses[200 + k] != 399) {// 保证顺序执行后不跨区间
				insAddresses[200 + k + 1] = insAddresses[200 + k] + 1;
			}else {
				insAddresses[200 + k + 1] = insAddresses[200 + k];
			}
			k += 2;
		}
		
		return insAddresses;
	}

}
