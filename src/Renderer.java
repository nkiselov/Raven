import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class Renderer {
    private static final int IMAGE_TYPE = BufferedImage.TYPE_4BYTE_ABGR;
    private static final double BORDER_SCALE = 0.02;
    private static final double BLANK_SCALE = 0.2;
    private static final double OBJECT_INNER_SCALE = 0.6;
    private static final double OBJECT_SIZE = 0.2;

    public static BufferedImage renderMatrix(Cell[][] matrix, int side){
        BufferedImage img = new BufferedImage(side,side,IMAGE_TYPE);
        Graphics2D gfx = (Graphics2D) img.getGraphics();
        renderMatrix(gfx,matrix,0,0,side);
        gfx.dispose();
        return img;
    }

    public static BufferedImage renderCell(Cell cell, int side){
        BufferedImage img = new BufferedImage(side,side,IMAGE_TYPE);
        Graphics2D gfx = (Graphics2D) img.getGraphics();
        renderCell(gfx,cell,0,0,side);
        gfx.dispose();
        return img;
    }

    public static void renderMatrix(Graphics2D gfx, Cell[][] matrix, int sx, int sy, int side){
        int blank = (int)(BLANK_SCALE*side/3);
        while((side-2*blank) % 3 != 0){
            blank++;
        }
        int cellSide = (side-2*blank)/3;
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                int ssx = sx+(cellSide+blank)*j;
                int ssy = sy+(cellSide+blank)*i;
                renderCell(gfx,matrix[i][j],ssx,ssy,cellSide);
            }
        }
    }

    public static void renderCell(Graphics2D gfx, Cell cell, int sx, int sy, int side){
        int bord = (int)(BORDER_SCALE*side);
        gfx.setColor(Color.BLACK);
        gfx.fillRect(sx,sy,side,side);
        int a = side-2*bord;
        sx+=bord;
        sy+=bord;
        gfx.setColor(Color.WHITE);
        gfx.fillRect(sx,sy,a,a);
        for(Map.Entry<String,Object> pair:cell.attributes.entrySet()){
            switch(pair.getKey()){
                case Generator.OBJECT_POSITION_TAG:
                    int o = (Integer)pair.getValue();
                    for(int i=0; i<3; i++){
                        for(int j=0; j<3; j++){
                            if((o & (1<<(3*i+j)))>0) {
                                drawSquare(gfx, sx+a*((1-OBJECT_INNER_SCALE)/2+0.5*OBJECT_INNER_SCALE*j),sy+a*((1-OBJECT_INNER_SCALE)/2+0.5*OBJECT_INNER_SCALE*i),side*OBJECT_SIZE);
                            }
                        }
                    }
                    break;
            }
        }
    }

    private static void drawSquare(Graphics2D gfx, double mx, double my, double side){
        double sx = mx-side/2;
        double sy = my-side/2;
        double ex = mx+side/2;
        double ey = my+side/2;
        gfx.setColor(Color.BLACK);
        gfx.fillRect((int)sx,(int)sy,(int)(ex-sx),(int)(ey-sy));
    }
}
