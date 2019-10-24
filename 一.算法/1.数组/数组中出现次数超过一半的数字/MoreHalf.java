题目：数组中有一个数字出现的次数超过数组长度的一半，请找出这个数字。
例如输入一个长度为9的数组{1,2,3,2,2,2,5,4,2}。由于数字2在数组中出现了5次，超过数组长度的一半，因此输出2。

1.解法一：基于partition函数的O(n)解法
如果数组中有一个数字出现次数超过数组的一半，如果我们把这个数组排序，则该数一定出现在数组的中间位置，即在一个长度为n的数组中找第n/2的数字，我们有
成熟的O(n)算法得到数组中第n/2大小的数。

public static int findOverMid(int[] arr){
  if(arr == null || arr.length == 0){
    return Integer.MIN_VALUE;
  }
  int mid = arr.length/2;
  
  return quickSort(arr, 0, arr.length-1, mid);
}

public static int quickSort(int[] arr, int low, int high, int k){
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
  }else if(i < k-1){
    return quickSort(arr, i+1, high, k);
  }else{
    return quickSort(arr, low, i-1, k);
  }
}

2.解法二：根据数组特点找出O(n)的算法
如果一个数字在数组中出现的次数超过数组的一半，那么其他所有数出现的次数都没有这个数字多。
我们可以从头遍历数组，保存两个值：一个为数字，另一个为次数。
（1）如果我们遍历到下一个数字，与保存数字相同，则次数+1；
（2）如果不同，则次数-1；
（3）若数字为0，则把数字赋值下一个数字；
这样，当遍历完数组后，数字值存储的即为我们要的。
代码如下：
public static moreThanHalf(int[] arr){
  if(arr == null){
     return Integer.MIN_VALUE;
  }
  
  int number = arr[0];
  int count = 1;
  for(int i = 1; i < arr.length;i++){
    if(number == arr[i]){
      count++;
    }else{
      count--;
      if(count == 0){
        number = arr[i];
        count = 1;
      }
    }
  }
  return number;
}






