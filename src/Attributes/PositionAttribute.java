package Attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PositionAttribute implements Attribute<Integer, PositionAttribute.PositionScheme>{
    private String tag;
    private static final PositionScheme[] schemes = PositionScheme.class.getEnumConstants();
    private int size;
    private int s1;

    public PositionAttribute(String tag, int size) {
        this.tag = tag;
        this.size = size;
        s1 = 1<<size;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public PositionScheme[] getSchemes() {
        return schemes;
    }

    @Override
    public Layer<Integer> generateMatrix(PositionScheme scheme, int options) {
        System.out.println(scheme);
        main:
        while(true) {
            Integer[][] mat = new Integer[3][3];
            switch (scheme) {
                case POSITION_DUAL_XOR:
                    for(int i=0; i<3; i++){
                        mat[i][0] = generatePosition();
                        mat[i][1] = generatePosition();
                        mat[i][2] = mat[i][0] ^ mat[i][1];
                    }
                    break;
                case POSITION_DUAL_OR:
                    for(int i=0; i<3; i++){
                        mat[i][0] = generatePosition();
                        mat[i][1] = generatePosition();
                        mat[i][2] = mat[i][0] | mat[i][1];
                    }
                    break;
                case POSITION_DUAL_AND:
                    for(int i=0; i<3; i++){
                        mat[i][0] = generatePosition();
                        mat[i][1] = generatePosition();
                        mat[i][2] = mat[i][0] & mat[i][1];
                    }
                    break;
                case POSITION_UNION_OR:
                    int u2 = generatePosition();
                    for(int i = 0; i<3; i++){
                        int[] cov = new int[3];
                        int r = u2;
                        for(int j=1; r>0; j=j<<1){
                            if((r&j)>0){
                                r-=j;
                                cov[(int)(Math.random()*3)]+=j;
                            }
                        }
                        for(int j=0; j<3; j++){
                            mat[i][j] = cov[j]|(u2&generateMask());
                        }
                    }
                    break;
//                case POSITION_UNION_AND:
//                    int u = generatePosition();
//                    for(int i = 0; i<3; i++){
//                        int[] cov = new int[3];
//                        int r = (~u) & (s1 -1);
//                        for(int j=1; r>0; j=j<<1){
//                            if((r&j)>0){
//                                r-=j;
//                                cov[(int)(Math.random()*3)]+=j;
//                            }
//                        }
//                        for(int j=0; j<3; j++){
//                            mat[i][j] = cov[j]|u;
//                        }
//                    }
//                    break;
                case COUNT_PROGRESSION:
                    List<Integer> cnts = new ArrayList<>();
                    for(int i = 0; i<= size; i++){
                        cnts.add(i);
                    }
                    int a = cnts.remove((int)(Math.random()*cnts.size()));
                    int b = cnts.remove((int)(Math.random()*cnts.size()));
                    int c = cnts.remove((int)(Math.random()*cnts.size()));
                    int mn = 1-Math.min(a,Math.min(b,c));
                    int mx = size-1-Math.max(a,Math.max(b,c));
                    int[] offs = new int[]{
                      0,(int)(Math.random()*(mx-mn+1))+mn,(int)(Math.random()*(mx-mn+1))+mn
                    };
                    for(int i=0; i<3; i++){
                        int off = offs[i];
                        mat[i][0] = generatePosition(a+off);
                        mat[i][1] = generatePosition(b+off);
                        mat[i][2] = generatePosition(c+off);
                    }
                    break;
                case COUNT_UNION:
                    List<Integer> cnts2 = new ArrayList<>();
                    for(int i = 0; i<= size; i++){
                        cnts2.add(i);
                    }
                    for(int i = 0; i<= size -3; i++){
                        cnts2.remove((int)(Math.random()*cnts2.size()));
                    }
                    for(int i=0; i<3; i++){
                        Collections.shuffle(cnts2);
                        for(int j=0; j<3; j++){
                            mat[i][j] = generatePosition(cnts2.get(j));
                        }
                    }
                    break;
            }
            for(PositionScheme sc : schemes){
                if(sc != scheme){
                    if(testMatrix(mat,sc)){
                        continue main;
                    }
                }
            }
            Integer[] ops = new Integer[options];
            int c = 1;
            ops[0] = mat[2][2];
            for(int i=0; i<(options-1)*2; i++){
                int op = generatePosition();
                if(contains(ops,op)){
                    continue;
                }
                mat[2][2] = op;
                boolean good = true;
                for(PositionScheme sc : schemes){
                    if(testMatrix(mat,sc)){
                        good = false;
                        break;
                    }
                }
                if(good){
                    ops[c] = op;
                    c++;
                    if(c==options){
                        mat[2][2] = 0;
                        return new Layer<>(mat,ops);
                    }
                }
            }
        }
    }

    @Override
    public Layer<Integer> generateDistraction(int options) {
        while (true) {
            Integer[][] mat = new Integer[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    mat[i][j] = generatePosition();
                }
            }
            Integer[] ops = new Integer[options];
            int c = 0;
            for(int i=0; i<options*2; i++){
                int op = generatePosition();
                if(contains(ops,op)){
                    continue;
                }
                mat[2][2] = op;
                boolean good = true;
                for(PositionScheme sc : schemes){
                    if(testMatrix(mat,sc)){
                        good = false;
                        break;
                    }
                }
                if(good){
                    ops[c] = op;
                    c++;
                    if(c==options){
                        mat[2][2] = 0;
                        return new Layer<>(mat,ops);
                    }
                }
            }
        }
    }

    private boolean contains(Integer[] arr, int v){
        for(Integer a:arr){
            if(a!=null && v==a){
                return true;
            }
        }
        return false;
    }

    private int generatePosition(){
        return (int)(Math.random()*(s1 -2))+1;
    }

    private int generateMask(){
        return (int)(Math.random()*(s1));
    }

    private int generatePosition(int n){
        int mx = choose(size,n);
        return decode(n,(int)(Math.random()*mx));
    }

    public boolean testMatrix(Integer[][] matrix, PositionScheme scheme){
        switch(scheme){
            case POSITION_DUAL_XOR:
                for(int i=0; i<3; i++){
                    if((matrix[i][0] ^ matrix[i][1]) != matrix[i][2]){
                        return false;
                    }
                }
                return true;
            case POSITION_DUAL_OR:
                for(int i=0; i<3; i++){
                    if((matrix[i][0] | matrix[i][1]) != matrix[i][2]){
                        return false;
                    }
                }
                return true;
            case POSITION_DUAL_AND:
                for(int i=0; i<3; i++){
                    if((matrix[i][0] & matrix[i][1]) != matrix[i][2]){
                        return false;
                    }
                }
                return true;
            case POSITION_UNION_OR:
                int u = matrix[0][0] | matrix[0][1] | matrix[0][2];
                for(int i=1; i<3; i++){
                    if((matrix[i][0] | matrix[i][1] | matrix[i][2]) != u){
                        return false;
                    }
                }
                return true;
//            case POSITION_UNION_AND:
//                int u2 = matrix[0][0] & matrix[0][1] & matrix[0][2];
//                for(int i=1; i<3; i++){
//                    if((matrix[i][0] & matrix[i][1] & matrix[i][2]) != u2){
//                        return false;
//                    }
//                }
//                return true;
            case COUNT_PROGRESSION:
                int p1 = Integer.bitCount(matrix[0][1])-Integer.bitCount(matrix[0][0]);
                int p2 = Integer.bitCount(matrix[0][2])-Integer.bitCount(matrix[0][1]);
                for(int i=1; i<3; i++){
                    if((Integer.bitCount(matrix[i][1])-Integer.bitCount(matrix[i][0])) != p1){
                        return false;
                    }
                    if((Integer.bitCount(matrix[i][2])-Integer.bitCount(matrix[i][1])) != p2){
                        return false;
                    }
                }
                return true;
            case COUNT_UNION:
                int[] cnt = new int[size +1];
                for(int j=0; j<3; j++){
                    cnt[Integer.bitCount(matrix[0][j])]++;
                }
                for(int i=0; i<3; i++){
                    int[] cnt2 = new int[size +1];
                    for(int j=0; j<3; j++){
                        cnt2[Integer.bitCount(matrix[i][j])]++;
                    }
                    for(int j = 0; j<= size; j++){
                        if(cnt[j] != cnt2[j]){
                            return false;
                        }
                    }
                }
                return true;
        }
        return false;
    }

    static int decode(int ones, int ordinal)
    {
        int bits = 0;
        for (int bit = 8; ones > 0; --bit)
        {
            int nCk = choose(bit, ones);
            if (ordinal >= nCk)
            {
                ordinal -= nCk;
                bits |= 1 << bit;
                --ones;
            }
        }
        return bits;
    }

    static int choose(int n, int m){
        if(n<m){
            return 0;
        }
        int c = 1;
        int mn = Math.min(n-m,m);
        int mx = Math.max(n-m,m);
        for(int i=mx+1; i<=n; i++){
            c*=i;
        }
        for(int i=1; i<=mn; i++){
            c/=i;
        }
        return c;
    }

    public enum PositionScheme{
        POSITION_DUAL_XOR,
        POSITION_DUAL_OR,
        POSITION_DUAL_AND,
        POSITION_UNION_OR,
        //POSITION_UNION_AND,
        COUNT_PROGRESSION,
        COUNT_UNION
    }
}
