import Attributes.Layer;
import Attributes.PositionAttribute;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        int side = 200;
        int options = 8;
        Layer<Cell> mat = Generator.generateRaven(1,0,options);
        BufferedImage img = Renderer.renderMatrix(mat.matrix,side);
        ImageIO.write(img,"png",new File("raven.png"));
        BufferedImage pan = new BufferedImage(side*options,side,BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D gfx = (Graphics2D)pan.getGraphics();
        List<Integer> ps = new ArrayList<>();
        for(int i=0; i<options; i++){
            ps.add(i);
        }
        for(int i=0; i<options; i++){
            int s = ps.remove((int)(Math.random()*ps.size()));
            if(s==0){
                System.out.println(i);
            }
            Renderer.renderCell(gfx,mat.options[s],side*i,0,side);
        }
        ImageIO.write(pan,"png",new File("answers.png"));
//        PositionAttribute at = new PositionAttribute("9",9);
//        Layer<Integer> l = at.generateMatrix(PositionAttribute.PositionScheme.COUNT_UNION,8);
//        for(int i=0; i<3; i++){
//            for(int j=0; j<3; j++){
//                System.out.println(Integer.bitCount(l.matrix[i][j]));
//            }
//        }
//        for(int i=0; i<l.options.length; i++){
//            System.out.println(Integer.bitCount(l.options[i])+": "+l.options[i]);
//        }
//        Integer[][] mat = new Integer[][]{
//                new Integer[]{0b111000111,0b100100100,0b100000100},
//                new Integer[]{0b111000111,0b100100100,0b100000100},
//                new Integer[]{0b111000111,0b100100100,0b100000100},
//        };
//        for(PositionAttribute.PositionScheme sch : at.getSchemes()){
//            if(at.testMatrix(mat,sch)){
//                System.out.println(sch.toString());
//            }
//        }
    }
}
