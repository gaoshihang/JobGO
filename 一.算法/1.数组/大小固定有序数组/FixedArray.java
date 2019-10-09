public class FixedArray {

    private int[] data;
    private int size;

    public FixedArray(int capacity){
        size = 0;
        data = new int[capacity];
    }

    //插入一个元素，找到位置
    public void insert(int element){
        if(size == data.length){
            System.out.println("数组已满");
            return;
        }

        if(size == 0){
            data[0] = element;
            size++;
            return;
        }

        for(int i = size - 1; i >= 0; i--){
            if(element < data[i]){
                data[i+1] = data[i];
            }else{
                data[i+1] = element;
                break;
            }
        }


        size++;
    }

    //删除指定位置元素
    public int remove(int index){
        if(size == 0){
            System.out.println("数组为空");
            return -1;
        }

        int res = data[index];
        for(int i = index; i < size;i++){
            data[i] = data[i+1];
        }

        size--;
        return res;
    }

    public void print(){
        for(int i = 0; i < size;i++){
            System.out.print(data[i] + " ");
        }
        System.out.println();
    }
    
}
