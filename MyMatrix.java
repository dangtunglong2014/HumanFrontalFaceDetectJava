/*Author: Long Dang */
/*Version: 2.0*/
/*Starting date: 1/08/2017 */

import java.io.*;  
import java.util.Scanner;

public class MyMatrix
{
	private double[][] data; // all elements of matrix
	private int row; // number of rows of matrix
	private int col; // number of columns of matrix

	public MyMatrix(){
		data = null;
		row = 0;
		col = 0;
	}

	public MyMatrix(int rows, int cols, String textName) throws IOException {
		// read matrix from a text file
		row = rows;
		col = cols;

			/*locate 2-dimensional dynamic array*/
			double[][] d = new double [row][col];
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
			/*initialize index of buffers*/
			int natIndex = 0, decIndex = 0, expIndex = 0;
			double absolute = 0, expValue = 1;
			double divisor = 1;
			int indexRow = 0, indexCol = 0;
	
		/*read text file char by char until last char is read*/
		int r = 0;
		while ((r = br.read()) != -1)
		{
			c = (char)r; // read charactor
			if (c == ',' || c == '\n')
			{
				/*Copy to d*/
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
					d[indexRow][indexCol] = -absolute * expValue;
				}
				else
				{
					d[indexRow][indexCol] = absolute * expValue;
				}
				/*Reset index and flag*/
				natIndex = 0;
				decIndex = 0;
				expIndex = 0;
				expValue = 1;
				divisor = 1;
				negative = 0;
				negativeExp = 0;
				exponent = 0;
				dot = 0;
				indexCol++;
				if (indexCol == col)
				{
					indexRow++;
					indexCol = 0;
				}
				if (indexRow == row)
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
						/*The element is negative*/
						negative = 1;
					}
					else
					{
						/*The exponent is negative*/
						negativeExp = 1;
					}
				}
				else
				{
					if (c == 'e')
					{
						/*The next chars is at exponent part of element*/
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
								/*The next chars is at the fraction part of element*/
								dot = 1;
								continue;
							}
							else // meet digit
							{
								if (exponent == 0)
								{
									/*The digit is at the base part of element*/
									if (dot == 0)
									{
										/*The digit is at the natural part of element*/
										natIndex++;
										naturalPart[natIndex] = naturalPart[natIndex - 1] * 10 + (double)(c - 48);
									}
									else
									{
										/*The digit is at the fraction part of element*/
										decIndex++;
										decimalFraction[decIndex] = decimalFraction[decIndex - 1] * 10 + (double)(c - 48);
										divisor = divisor * 10;
									}
								}
								else
								{
									/*The digit is at the exponent part of element*/
									expIndex++;
									expPart[expIndex] = expPart[expIndex - 1] * 10 + (double)(c - 48);
								}
							}
						}
					}
				}
			}
		}
		data = d;
	}

	public double[][] getData(){
		return data;
	}

	public int getRow(){
		return row;
	}

	public void print_out(){
		// for test only
		for (int i = 0; i < row; i++){
			for (int j = 0; j < col; j++){
				System.out.print(data[i][j]);
				System.out.print(";");
			}
			System.out.println();
		}
	}
};

