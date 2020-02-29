/*Author: Long Dang */
/*Version: 2.0 (using Java)*/
/*Starting date: 1/08/2017 */

import java.io.*;  
import java.util.Scanner; 
import java.lang.Math;

public class MyVector{

	private double[] data; // containing all elements of the vector
	private int length; // length of data
	private int[] coordinate; // coordinate[x1][y1][size of square]
	private MyVector next; // pointer points to next element

	private void tansig(){
		for (int i = 0; i < length; i++){
			data[i] = 2 / (1 + Math.exp(-2 * data[i])) - 1;
		}
	}

	private void add(MyVector v){
		for (int i = 0; i < length; i++){
			data[i] = data[i] + v.getData()[i];
		}
	}

	private void mapminmax(MyVector min, MyVector max){ // preprocessing
		for (int i = 0; i < length; i++){
			data[i] = 2 * (data[i] - min.getData()[i]) / (max.getData()[i] - min.getData()[i]) - 1;
		}
	}

	private void mulMatrix(MyMatrix m){ // multiply m * vector
		double[] d = new double[m.getRow()];
		for (int i = 0; i < m.getRow(); i++){
			d[i] = 0;
			for (int j = 0; j < length; j++){
				d[i] = d[i] + data[j] * m.getData()[i][j];
			}
		}
		data = d;
		length = m.getRow();
	}



	public MyVector(){
		data = null;
		length = 0;
		coordinate = null;
		next = null;
	}

	public MyVector(int l, String textName) throws IOException {		 // for reading a vector from text file
			/*new dynamic array to save all elements of vector*/
			double[] d = new double[l]; 
	
			/*open text file*/
			File f = new File(textName); 
			FileReader fr = new FileReader(f);   //Creation of File Reader object
      		BufferedReader br = new BufferedReader(fr);  //Creation of BufferedReader object

			char c;
	
			/*initialize flags*/
			int negative = 0; // negative flag
			int negativeExp = 0; // negative exponent flag
			int exponent = 0; // exponent flag
			int dot = 0; // dot flag
	
			/*initialize buffers*/
			double[] naturalPart = new double[20];
			double[] decimalFraction = new double[20];
			double[] expPart = new double[20];

			for (int i = 0; i < 20; i++)
			{
				naturalPart[i] = 0;
				decimalFraction[i] = 0;
				expPart[i] = 0;
			}
	
			/*initilize index of buffers*/
			int natIndex = 0, decIndex = 0, expIndex = 0;
			double absolute = 0, expValue = 1;
			double divisor = 1;
			int index = 0;
	
		/*traverse the fixe char by char until the last char is read*/
		int r = 0;
		while ((r = br.read()) != -1)
		{
			c = (char)r;
			if (c == ',' || c == '\n') // copy to d when meet , or \n 
			{
				absolute = naturalPart[natIndex] + decimalFraction[decIndex] / divisor;
				for (int j = 0; j < expPart[expIndex]; j++)
				{
					expValue = expValue * 10;
				}
				if (negativeExp == 1)
				{
					expValue = 1 / expValue;
				}
				if (negative == 1)
				{
					d[index] = -absolute * expValue;
				}
				else
				{
					d[index] = absolute * expValue;
				}
				/*reset indexs*/
				index++;
				natIndex = 0;
				decIndex = 0;
				expIndex = 0;
				expValue = 1;
				divisor = 1;
				negative = 0;
				negativeExp = 0;
				exponent = 0;
				dot = 0;
				if (index == l) // stop when index reach length of vector
				{
					break;
				}
				continue;
			}
			else
			{
				if (c == '-')
				{
					if (exponent == 0)
					{
						/*the element is negative*/
						negative = 1;
					}
					else
					{
						/*the exponent of the element is negative*/
						negativeExp = 1;
					}
				}
				else
				{
					if (c == 'e')
					{
						/*the next chars is at exponent part of element*/
						exponent = 1;
						continue;
					}
					else
					{
						if (c == '+')
						{
							/*no meaning*/
							continue;
						}
						else
						{
							if (c == '.')
							{
								/*the next chars is at the fraction part of element*/
								dot = 1;
								continue;
							}
							else // met digits
							{
								if (exponent == 0)
								{
									/*the digit is at the base part of the element*/
									if (dot == 0)
									{
										/*the digit is at the natural part of the element*/
										natIndex++;
										naturalPart[natIndex] = naturalPart[natIndex - 1] * 10 + (double)(c - 48);
									}
									else
									{
										/*the digit is at the fraction part of the element*/
										decIndex++;
										decimalFraction[decIndex] = decimalFraction[decIndex - 1] * 10 + (double)(c - 48);
										divisor = divisor * 10;
									}
								}
								else
								{
									/*the digit is at the exponent part of the element*/
									expIndex++;
									expPart[expIndex] = expPart[expIndex - 1] * 10 + (double)(c - 48);
								}
							}
						}
					}
				}
			}
		}
		data = d; // data points as d
		length = l; // update length of data
		coordinate = null;
		next = null;
	}

	public MyVector(double[] d, int l, int[] c){ // double data, int length, int coordinate
		data = d;
		length = l;
		coordinate = c;
	}

	/*parse vector through a neural network, including preprocessing*/
	public void neuralNetwork(MyMatrix eigen, MyVector minInput, MyVector maxInput, MyMatrix IW11, MyVector b1, MyMatrix LW21, MyVector b2){
		mulMatrix(eigen);
		mapminmax(minInput, maxInput);
		mulMatrix(IW11);
		add(b1);
		tansig();
		mulMatrix(LW21);
		add(b2);
		tansig();
	}

	public double[] getData(){
		return data;
	}

	public int[] getCoordinate(){
		return coordinate;
	}

	public MyVector getNext(){
		return next;
	}

	public void setNext(MyVector v){
		next = v;
	}

	public void print_out(){
		// for test only
		for (int i = 0; i < length; i++){
			System.out.println(data[i]);
		}
	}
}