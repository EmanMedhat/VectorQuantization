package vector.quantization;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;
import javax.imageio.ImageIO;

class VectorQuantization
{
    public  int [][] originalImage ;
    public VectorQuantization()
    {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the width of the block");
       int widthOfBlock =Integer.parseInt(sc.nextLine());
        System.out.println("Enter the height of block");
       int heightOfBlock = Integer.parseInt(sc.nextLine());
        System.out.println("Enter number of levels ");
       int numOfLevels =Integer.parseInt(sc.nextLine());

        originalImage  = readImage("H:\\java\\Vector Quantization/img.jpg");

        int numOfRows = originalImage.length /heightOfBlock ; // hight for new matrix from vectors
        int numOfCols = originalImage[0].length /heightOfBlock ; // width for new matrix from vectors
        Vector [][] vectors = new Vector [numOfRows][numOfCols]; // 2D array consist of vectors

        ArrayList <Vector> data = Build_vectors (originalImage , vectors , numOfRows , numOfCols , widthOfBlock , heightOfBlock );
        Quantization (numOfLevels , data , widthOfBlock , heightOfBlock ,vectors , numOfRows , numOfCols  );
    }
    
     ArrayList <Vector> Build_vectors (int [][] originalImage , Vector [][] vectors , int NumOfRows , int NumOfColumns , int widthOfBlock , int heightOfBlock)
    {
        ArrayList <Vector> AllVectors = new ArrayList<>();
        Vector currentVector = new Vector ( widthOfBlock , heightOfBlock );

        for (int i=0 ; i<originalImage.length ; i=i+heightOfBlock) //h
        {
            for (int j=0 ; j<originalImage[0].length ; j=j+widthOfBlock) //w
            {
            int x = i ;
            int z = j ;
            currentVector = new Vector ( widthOfBlock , heightOfBlock );

            for (int n=0 ; n<heightOfBlock ; n++)
            {
                for (int m=0 ; m<widthOfBlock ; m++)
                {
                    currentVector.data[n][m]= originalImage[x][z];
                }
                x++;
                z=j;
            }
            AllVectors.add(currentVector);
            }
        }
        //set index for each vector..block

        int index =0 ;

        for (int i=0 ; i<NumOfRows ; i++)
        {
            for (int j=0 ; j<NumOfColumns ; j++)
            {
                vectors[i][j] = AllVectors.get(index++);
            }
        }
        return AllVectors ;
    }
     
     void Quantization ( int numoflevels ,  ArrayList <Vector> data , int widthOfBlock , int heightOfBlock , Vector [][] vectors , int numOfRows , int numOfCols  )
    {
        ArrayList <Vector> Averages = new ArrayList<>();
        Vector first_average = new Vector( widthOfBlock , heightOfBlock );

        for (int w = 0; w < widthOfBlock; w++)
        {
            for (int h = 0; h < heightOfBlock; h++)
            {
                double total = 0 ;
                for (int i = 0; i < data.size(); i++)
                {
                    total = total + data.get(i).data[w][h];
                }
                first_average.data[w][h] = total/data.size();  //t/9block
            }
        }
        Averages.add(first_average);
        Averages = Split (Averages , data , numoflevels );
        System.out.print("\n");
        System.out.println("Splited");

        System.out.println("Averages: ");
        for (int i=0 ; i<Averages.size()  ; i++)
        {
            ShowVector(Averages.get(i));
        }

        ArrayList<Vector> previous_Averages = Averages ;
        ArrayList<Vector> new_Averages = associate( Averages , data);

        new_Averages = modify(previous_Averages, new_Averages, data);

        System.out.println("New Average is ");

        for (int x=0 ; x<new_Averages.size() ; x++)
        {
            ShowVector(new_Averages.get(x));
        }

        ArrayList <Vector> CodeBook = new ArrayList<>();

        for (int i=0 ; i<new_Averages.size() ; i++)
        {
            CodeBook.add(new_Averages.get(i));
        }

        int index =0 ;
        for (int i=0 ; i<widthOfBlock ; i++)
        {
            for (int j=0 ; j<numOfCols ; j++)
            {
                vectors[i][j] = data.get(index++);
            }
        }
        compress (CodeBook , vectors );
    }
     
      // split original averages
    ArrayList<Vector> Split (ArrayList <Vector> Averages ,  ArrayList <Vector> data , int numoflevels )
    {
        int width = Averages.get(0).width ;
        int height = Averages.get(0).height ;

        for (int i=0 ; i<Averages.size() ; i++)
        {
            if (Averages.size()<numoflevels)
            {
                ArrayList <Vector> split = new ArrayList<>();
                for (int j=0 ; j<Averages.size() ; j++)
                {
                    Vector left = new Vector( width , height);
                    Vector right = new Vector( width , height);

                    for (int w=0 ; w<width ; w++)
                    {
                        for (int h=0 ; h<height ; h++)
                        {
                            int cast = (int)Averages.get(j).data[w][h] ;
                            left.data[w][h]= cast;
                            right.data[w][h]= cast+1;
                        }

                    }
                    split.add(left);
                    split.add(right);
                }

                Averages.clear();
                Averages = associate( split , data);
                i=0 ;
            }
            else
                break;
        }
        return Averages ;
    }
     
    void ShowVector (Vector v)
    {
        for (int i=0 ; i<v.height ; i++ )
        {
            for (int j=0 ; j<v.width ; j++)
            {
                System.out.print(v.data[i][j] + "  ");
            }
            System.out.println();
        }

        System.out.println("---------------------------");
    }
     
     void compress (ArrayList<Vector> codeBook , Vector [][] vectors)
    {
        int Rows = vectors.length ;
        int Columns = vectors[0].length ;
        int [][] compress_image = new int [Rows][Columns];

        for (int i=0 ; i<Rows ; i++)
        {
            for (int j=0 ; j<Columns ; j++)
            {
                Vector current = vectors[i][j];
                ArrayList <Double> distance_difference = new ArrayList<> ();
                for (int k=0 ; k<codeBook.size() ;k++)
                {
                    double total_diffrence = 0 ;
                    for (int w=0 ; w<codeBook.get(0).width ; w++)
                    {
                        for (int h = 0; h < codeBook.get(0).height; h++)
                        {
                            double value = current.data[w][h] - codeBook.get(k).data[w][h];
                            double distanc_diffrence = Math.pow(value, 2);
                            total_diffrence = total_diffrence + distanc_diffrence;
                        }
                    }
                    distance_difference.add(total_diffrence);
                }
                int index = indxOF_min_distance (distance_difference);
                compress_image[i][j]= index ;
            }
        }
        Save_CodeBook_CompressImg( codeBook , compress_image);
    }
     
     void Save_CodeBook_CompressImg(ArrayList<Vector> codeBook , int [][] compress_image )
    {
        Openfile("H:\\java\\Vector Quantization/CompressFile.txt");
        String codeBookSize = "" + codeBook.size();
        String WidthOfBlock = "" + codeBook.get(0).width;
        String HeightOfBlock = "" + codeBook.get(0).height;
        write(codeBookSize);
        write(WidthOfBlock);
        write(HeightOfBlock);
        for (int i=0 ; i<codeBook.size() ; i++)
        {
            for (int w=0 ; w<codeBook.get(i).width ; w++)
            {
                String row = "";
                for (int h=0 ; h<codeBook.get(i).height ; h++)
                {
                    row = row + codeBook.get(i).data[w][h] + " ";
                }
                write(row);
            }
        }
        //compress the height of the image
        String compress_image_height = "" + compress_image.length ;
        write(compress_image_height);
        //compress the width of the image
        String compress_image_width = "" + compress_image[0].length ;
        write(compress_image_width);
        for (int i=0 ; i<compress_image.length ; i++)
        {
            String row = "";
            for (int j=0 ; j<compress_image[0].length ; j++)
            {
                row+= compress_image[i][j] +" ";
            }
            write(row);
        }
        Closefile();
    }

   
    int indxOF_min_distance (ArrayList <Double> distance_difference )
    {
        double min_diff = distance_difference.get(0); // assume first element is the min
        int index = 0 ;

        for (int i=1 ; i<distance_difference.size() ; i++)
        {
            if ( distance_difference.get(i) < min_diff)
            {
                min_diff = distance_difference.get(i);
                index = i ;
            }

        }
        return index ;
    }

    ArrayList<Vector> associate ( ArrayList<Vector> split , ArrayList <Vector> data  ) // associate ang return avg
    {
        ArrayList <splitElement> Split = new ArrayList<>();
        ArrayList <Vector> Averages = new ArrayList<> ();
        int width = data.get(0).width;
        int height = data.get(0).height ;

        for (int i = 0; i < split.size(); i++)  // inilialization
        {
            splitElement initial = new splitElement() ;
            initial.setValue(split.get(i));
            Split.add(initial);
        }

        for (int i=0 ; i<data.size() ; i++) // associate data
        {
            Vector current = data.get(i);
            ArrayList <Double> distance_difference = new ArrayList<> ();


            for (int j=0 ; j<split.size() ;j++)
            {
                double total_diff = 0 ;

                for (int w=0 ; w<width ; w++)
                {
                    for (int h=0 ; h<height ; h++)
                    {
                        double value = current.data[w][h]-split.get(j).data[w][h];
                        double distanc_diff =  Math.pow( value , 2);
                        total_diff +=distanc_diff ;
                    }
                }
                distance_difference.add(total_diff);
            }
            int index = indxOF_min_distance (distance_difference);
            ArrayList <Vector> cur_associated = Split.get(index).getAssoicated();
            cur_associated.add(current);
            splitElement New = new splitElement(Split.get(index).getValue() , cur_associated);
            Split.set(index , New );

        }
        //for loop to get the average of the vectors
        for (int i=0 ; i<Split.size() ; i++)
        {
            int arraysize = Split.get(i).getAssoicated().size();
            Vector average = new Vector(width , height);
            for (int w = 0; w < width; w++)
            {
                for (int h = 0; h < height; h++)
                {
                    double total = 0 ;
                    for (int j = 0; j < arraysize; j++)
                    {
                        total+= Split.get(i).getAssoicated().get(j).data[w][h];
                    }
                    average.data[w][h]= total/arraysize;
                }
            }
            Averages.add(average);
        }
        return Averages ;
    }

   
    ArrayList<Vector> modify (ArrayList<Vector> PreviousAverages , ArrayList<Vector> new_Averages , ArrayList<Vector> data)
    {
        while (true)
        {
            int width = new_Averages.get(0).width;
            int height = new_Averages.get(0).height;
            int TotalDiffrence = 0 ;
            int AverageDiffrence = 0 ;

            for (int i=0 ; i<new_Averages.size() ; i++)
            {
                double DiffOf2vectors =0 ;
                for (int w=0 ; w<width ; w++)
                {
                    for (int h=0 ; h<height ; h++)
                    {
                        DiffOf2vectors += Math.abs(PreviousAverages.get(i).data[w][h] - new_Averages.get(i).data[w][h]) ;
                    }
                }
                TotalDiffrence+=DiffOf2vectors;
            }
            AverageDiffrence = TotalDiffrence / PreviousAverages.size() ;
            if (AverageDiffrence < 0.0001 )
            {
                break;
            }
            else
            {
                PreviousAverages = new_Averages ;
                new_Averages = associate( new_Averages , data);
            }
        }
        return new_Averages ;
    }
    
    //Decompress Function
    void Decompress ()
    {
        ArrayList<Vector> codeBook = new ArrayList <Vector>();
        int [][] compress_image = new int [1][1] ;
        compress_image = Reconstruct( codeBook , compress_image);
        int [][] Decompress_image = new int [originalImage.length][originalImage[0].length];

        for (int i=0 ; i<compress_image.length ; i++)
        {
            for (int j=0 ; j<compress_image[0].length ; j++)
            {
                Vector current = new Vector();
                current = codeBook.get(compress_image[i][j]);
                int cornerx = i*current.height;
                int cornery = j*current.width ;
                for (int h=0 ; h<current.height ; h++)
                {
                    for (int w=0 ; w<current.width ; w++)
                    {
                        Decompress_image[cornerx+h][cornery+w] = (int) current.data[h][w];
                    }
                }
            }
        }
        System.out.print("\n");
        System.out.println("The Decompressed Image");
        System.out.println(Decompress_image);
        writeImage(Decompress_image, "H:\\java\\Vector Quantization/img.jpg");
    }

    

    int [][] Reconstruct( ArrayList<Vector> codeBook , int [][] compress_image)
    {
        OpenFile("H:\\java\\Vector Quantization/CompressFile.txt");
        int codeBookSize = Integer.parseInt(sc.nextLine());
        int WidthOfBlock = Integer.parseInt(sc.nextLine());
        int HeightOfBlock = Integer.parseInt(sc.nextLine());

        for (int i=0 ; i<codeBookSize ; i++)
        {
            Vector current = new Vector(WidthOfBlock , HeightOfBlock);
            for (int w=0 ; w<WidthOfBlock ; w++)
            {
                String row = sc.nextLine();
                String [] elements = row.split(" ");

                for (int h=0 ; h<HeightOfBlock ; h++)
                {
                    current.data[w][h]= Double.parseDouble(elements[h]);
                }
            }
            codeBook.add(current);
        }

        int compress_image_height = Integer.parseInt(sc.nextLine());
        int compress_image_width =  Integer.parseInt(sc.nextLine());
        compress_image = new int [compress_image_height][compress_image_width];

        for (int i=0 ; i<compress_image.length ; i++)
        {
            String line = sc.nextLine();
            String [] row = line.split(" ");
            for (int j=0 ; j<compress_image[0].length ; j++)
            {
                compress_image[i][j] = Integer.parseInt(row[j]);
            }
        }
        CloseFile();
        return compress_image ;
    }
    
    // To read 2D int pixels from image file
    public int[][] readImage(String PathOfFile)
    {
        File file = new File(PathOfFile);
        BufferedImage image;
        int width , height;
        try
        {
            image = ImageIO.read(file);
            width = image.getWidth();
            height = image.getHeight();
            int[][] pixels = new int[height][width];
            int rgb;
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    rgb = image.getRGB(x, y);
                    pixels[y][x] = (rgb>>16) & 0xff; // to get red color as our gray scale
                    //pixels[y][x] = rgb & 0xff; // to get blue
                }
            }
            return pixels;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public void writeImage(int[][] pixels, String outputFilePath)
    {
        File fileoutput = new File(outputFilePath);
        int height = pixels.length;
        int width = pixels[0].length;
        BufferedImage image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image2.setRGB(x, y, (pixels[y][x]));
            }
        }
        try {
            ImageIO.write(image2, "png", fileoutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //function to open the file
    Scanner sc;
    public void OpenFile(String FileName) {
        try {
            sc = new Scanner(new File(FileName));
        } catch (Exception e) {

        }
    }

    //function to close the file
    public void CloseFile()
    {
        sc.close();
    }
    Formatter out;
    public void Openfile(String pass) {
        try {
            out = new Formatter(pass);
        } catch (Exception e) {
        }
    }
    public void Closefile() {
        out.close();
    }
    void write(String code) {
        out.format("%s", code);
        out.format("%n");
        out.flush();
    }

   
    public static void main(String[] args)
    {
        VectorQuantization v = new VectorQuantization();
        v.Decompress();
    }
}