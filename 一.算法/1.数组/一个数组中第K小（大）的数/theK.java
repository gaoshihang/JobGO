给定一个无序的数组，从一个数组中找出第k个最小的数，例如，数组{ 1,5,2,6,8,0,6 }, 其中第4小的数为5。

解法：剪枝法
采用快排的思路解决。选一个数tmp = arr[n-1],比它小的都放在左边，比它大的都放在右边，然后判断tmp的位置：
若它的位置在k-1，则它就是第K小的数；
若它的位置小于k-1，则要找的数在它的右边，递归的在它的右边查找；
若它的位置大于k-1，则要找的数在它的左边，递归的在它的左边查找；

以下为代码实现：
public static int theKSmall(int[] arr, int k){
  if(arr == null || arr.length < k){
    return Integer.MIN_VALUE;
  }
  
  return quickSort(arr, 0, arr.length-1, k);
}

public static int quickSort(int[] arr, int low, int high, int k){
  //第0个元素作为枢纽
  int i = low;
  int j = high;
  int tmp = arr[low];
  while(i < j){
    while(i < j && arr[j] >= tmp){
      j--;
    }
    if(i < j){
      arr[i++] = arr[j];
    }
    
    while(i < j && arr[i] < tmp){
      i++;
    }
    if(i < j){
      arr[j--] = arr[i];
    }
  }
  arr[i] = tmp;
  if(i == k-1){
    return tmp;
  }else if(i <= k-1){
    return quickSort(arr, i+1, high, k);
  }else{
    return quickSort(arr, low, i-1, k);
  }
}
