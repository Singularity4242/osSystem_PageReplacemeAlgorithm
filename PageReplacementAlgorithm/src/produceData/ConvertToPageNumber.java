package produceData;

public class ConvertToPageNumber {
	// 存储变换生成的页地址（页号）
	static int[] pageAddresses;
	
	// 变换生成400条页地址（页号）
	public static int[] convert(int[] insAddresses) {
		// 存储变换生成的400条页地址（页号）
		pageAddresses = new int[400];
		
		for(int i = 0; i < 400; i ++) {
			if(insAddresses[i] < 0 || insAddresses[i] >399) {
				System.out.println();
				System.out.println("指令数据中存在错误数据！");
				System.out.println();
			}else {
				pageAddresses[i] = insAddresses[i] / 10;
			}
		}
		
		return pageAddresses;
	}
	

}
