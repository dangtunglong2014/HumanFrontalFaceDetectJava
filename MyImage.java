/*Author: Long Dang */
/*Version: 2.0 (using Java)*/
/*Starting date: 1/08/2017 */


import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.util.*; 
import java.lang.Math;


public class MyImage{
	public static final int WINDOW_SIZE = 19; // edge of square window
	public static final int WINDOW_LENGTH = 361; // windowSize * windowSize
	public static final int INPUT_LENGTH = 80; // length of input of neural network
	public static final int HIDEN_LAYER_LENGTH = 40; // length of hidden layer
	public static final int OUTPUT_LENGTH = 1; // length of output

	private MyVector avg;
	private MyMatrix eigen;
	private MyMatrix IW11;
	private MyMatrix LW21;
	private MyVector b1;
	private MyVector b2;
	private MyVector minInput;
	private MyVector maxInput;

	private int isHumanSkin(int red, int green, int blue){
		if (red > 95 && green > 40 && blue > 20)
		{
			if ((red - green > 15) && (red > blue))
			{
				return 1;
			}
			return 0;
		}
		return 0;
	}

	private BufferedImage resizeImg(BufferedImage inputImg, double scale){
		// calculate absolute height and width of output imgage
		double inputHeight = inputImg.getHeight();
		double inputWidth = inputImg.getWidth();
		int scaledHeight = (int)(inputHeight * scale);
		int scaledWidth = (int)(inputWidth * scale);
		// creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImg.getType());
        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImg, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        return outputImage;
	}

	private int[] getPixel(BufferedImage img, int h, int w){
		int[] data = new int [3];
		// get pixel value
		int p = img.getRGB(w,h);
		//get alpha
        //int a = (p>>24) & 0xff;
        //get red
        int r = (p>>16) & 0xff;
        //get green
        int g = (p>>8) & 0xff;
        //get blue
        int b = p & 0xff;
        // import
        data[0] = r;
        data[1] = g;
        data[2] = b;
        return data;
	}

	private void setPixel(BufferedImage img, int w, int h, int alpha, int red, int green, int blue){
		//set the pixel value 
        int p = (alpha << 24) | (red << 16) | (green << 8) | blue; 
        img.setRGB(w, h, p); 
	}

	public MyImage() throws IOException {
		avg = new MyVector(WINDOW_LENGTH, "avg.txt");
		eigen = new MyMatrix(INPUT_LENGTH, WINDOW_LENGTH, "eigen.txt");
		IW11 = new MyMatrix(HIDEN_LAYER_LENGTH, INPUT_LENGTH, "IW11.txt");
		LW21 = new MyMatrix(OUTPUT_LENGTH, HIDEN_LAYER_LENGTH, "LW21.txt");
		b1 = new MyVector(HIDEN_LAYER_LENGTH, "b1.txt");
		b2 = new MyVector(OUTPUT_LENGTH, "b2.txt");
		minInput = new MyVector(INPUT_LENGTH, "minInput.txt");
		maxInput = new MyVector(INPUT_LENGTH, "maxInput.txt");
	}

	public void faceDetect(String imgName) throws IOException {
		System.out.println("Start Face Detection");

		// read image
		BufferedImage originalImg = null;
    	File f = null;
	    try{
	      f = new File(imgName);
	      originalImg = ImageIO.read(f);
		} catch(IOException e){
      		System.out.println(e);
    	}
    	// get image width and height
    	int originalWidth = originalImg.getWidth();
    	int originalHeight = originalImg.getHeight();
    	double scale = 1;
    	// reize to standard size
    	if (originalHeight* originalWidth > 540 * 960){
    		scale = Math.sqrt(540.0f * 960.0f / (originalHeight * originalWidth));
    	}
    	BufferedImage stdImg = resizeImg(originalImg, scale); 
    	// Scan
    	System.out.println("Start scan");

    	//List<MyVector> faceList = new ArrayList<MyVector>(); 
    	MyVectorList faceList = new MyVectorList();

    	for (int time = 0; time < 20; time++){
    		int height = stdImg.getHeight();
			int width = stdImg.getWidth();

			for (int h = 0; h < height - WINDOW_SIZE; h = h + 2){
				for (int w = 0; w < width - WINDOW_SIZE; w = w + 2){
					// Check skin color
					int[] c = getPixel(stdImg, h + WINDOW_SIZE/2, w + WINDOW_SIZE/2);
					if(isHumanSkin(c[0], c[1], c[2]) != 1){
						continue;
					}
					c = getPixel(stdImg, h + WINDOW_SIZE/3, w + WINDOW_SIZE/3);
					if(isHumanSkin(c[0], c[1], c[2]) != 1){
						continue;
					}
					c = getPixel(stdImg, h + WINDOW_SIZE/3, w + 2*WINDOW_SIZE/3);
					if(isHumanSkin(c[0], c[1], c[2]) != 1){
						continue;
					}
					c = getPixel(stdImg, h + 2*WINDOW_SIZE/3, w + WINDOW_SIZE/3);
					if(isHumanSkin(c[0], c[1], c[2]) != 1){
						continue;
					}
					c = getPixel(stdImg, h + 2*WINDOW_SIZE/3, w + 2*WINDOW_SIZE/3);
					if(isHumanSkin(c[0], c[1], c[2]) != 1){
						continue;
					}
					// Copy pixels to a vector
					double[] data = new double[WINDOW_LENGTH];
					int count = 0;
					for (int x = w; x < w + WINDOW_SIZE; x++){
						for (int y = h; y < h + WINDOW_SIZE; y++){
							c = getPixel(stdImg, y, x);
							int red = c[0];
							int green = c[1];
							int blue = c[2];
							/*Convert to rgb*/
							double gray = 0.2989 * red + 0.5870 * green + 0.1140 * blue;
							/*Save in a dynamic array*/
							data[count] = gray - avg.getData()[count];
							count++;
						}
					}
					// Scale coordinate
					int[] coordinate = new int[3];
					coordinate[0] = (int) (w / scale); // x1
					coordinate[1] = (int) (h / scale); // y1
					coordinate[2] = (int) ((double)WINDOW_SIZE / scale); // face size
					// Save data and cooridnate in a vector
					MyVector v = new MyVector(data, WINDOW_LENGTH, coordinate);
					// Neural network
					v.neuralNetwork(eigen, minInput, maxInput, IW11, b1, LW21, b2);
					// Enlist
					if (v.getData()[0] > 0.99999999){ 
						//faceList.add(v);
						faceList.insert(v);
					}
				}
			}

			// Stop-scanning condition
			if (time > 10 && faceList.isEmpty()){
				break;
			}
			// Zoom out
			scale = scale / 1.1;
			stdImg = resizeImg(originalImg, scale);

			System.out.println("Resized " + time + " time(s) " + stdImg.getHeight() + "x" + stdImg.getWidth());
    	}

    	System.out.println("Scan completed");

    	// Fix overlap
		faceList.fixOverlap(0.2);
		System.out.println("Total " + faceList.size() + " face(s) found");
		
		// Annote face position
		if(!faceList.isEmpty()){
			
			System.out.println("Start face anotation");
			//MyVector itr = new MyVector();
			MyVector itr = faceList.getRoot();
			//for (int idx = 0; idx < faceList.size(); idx++){
			while (itr != null){
				//itr = faceList.get(idx);
				int x1 = itr.getCoordinate()[0];
				int y1 = itr.getCoordinate()[1];
				int faceSize = itr.getCoordinate()[2];

				//System.out.println("Bordering");
				//top line
				for (int i = x1; i <= x1 + faceSize; i += 1){
					setPixel(originalImg, i, y1, 0, 0, 0, 0);
					setPixel(originalImg, i, y1 + 1, 0, 0, 0, 0);
					setPixel(originalImg, i, y1 + 2, 0, 0, 0, 0);
				}
				//right line
				for (int i = y1; i <= y1 + faceSize; i += 1){
					setPixel(originalImg, x1 + faceSize, i, 0, 0, 0, 0);
					setPixel(originalImg, x1 + faceSize + 1, i, 0, 0, 0, 0);
					setPixel(originalImg, x1 + faceSize + 2, i, 0, 0, 0, 0);				
				}
				//bottom line
				for (int i = x1; i <= x1 + faceSize; i += 1){
					setPixel(originalImg, i, y1 + faceSize, 0, 0, 0, 0);
					setPixel(originalImg, i, y1 + faceSize + 1, 0, 0, 0, 0);	
					setPixel(originalImg, i, y1 + faceSize + 2, 0, 0, 0, 0);			
				}
				//left line
				for (int i = y1; i <= y1 + faceSize; i += 1){
					setPixel(originalImg,  x1, i, 0, 0, 0, 0);
					setPixel(originalImg,  x1 + 1, i, 0, 0, 0, 0);
					setPixel(originalImg,  x1 + 2, i, 0, 0, 0, 0);
				}

				itr = itr.getNext();
			}

			System.out.println("Face anotation completed");

			// Write image with face anotation
			System.out.println("Saving face anotation file");

        	try {
    			File outputfile = new File("faceAnnotation.jpg");
    			ImageIO.write(originalImg, "jpg", outputfile);
			} catch (IOException e) {
    			System.out.println(e);
			}

			System.out.println("Face anotation file saved");
  		}
  		else{
  			System.out.println("No face detected");
  		}

  		System.out.println("Finish Face Detection");
	}
}