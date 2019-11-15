代码如下：  
```
public int findN(int n){
  if(n == 1) return 1;
  int result = findN(n-1) * n;
  return result;
}
```
