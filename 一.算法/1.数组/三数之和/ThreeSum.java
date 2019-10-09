题目：给定一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？找出所有满足条件且不重复的三元组。
分析：
1.首先对数组从小到大排序，固定一个位置nums[i]；再用左右指针分别指向i后面的两端，数字分别为nums[L]，nums[R]，计算三个数的和是否为0，是则添加；
2.如果nums[i]>0，则不可能为0；
3.如果nums[i] == nums[i+1]，说明会重复，跳过该数字；
4.当sum==0时，如果nums[L] == nums[L+1]，说明会重复，跳过该数字；如果nums[R] == nums[R-1]，说明会重复，跳过该数字；
5.时间复杂度为O(n^2)

代码：

/**
* 三数合并
* @param nums
* @return
*/
public static List<List<Integer>> threeSum(int[] nums) {
    List<List<Integer>> list = new ArrayList<>();
    if(nums.length < 3) return list;

    int length = nums.length;
    Arrays.sort(nums); //排序
    for(int i = 0; i < length;i++){
        if(nums[i] > 0) break; //如果当前数字大于0，则三数之和一定大于0，结束
        if(i > 0 && nums[i] == nums[i-1]) continue;
        int L = i + 1;
        int R = length - 1;
        while(L < R){
            int sum = nums[i] + nums[L] + nums[R];
            if(sum == 0) {
                list.add(Arrays.asList(nums[i], nums[L], nums[R]));

                while (L < R && nums[L] == nums[L + 1]) {
                    L++;
                }
                while (L < R && nums[R] == nums[R - 1]) {
                    R--;
                }

                L++;
                R--;
            }else if(sum < 0){
                L++;
            }else {
                R--;
            }
        }
    }

    return list;
}
