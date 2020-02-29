/*Author: Long Dang */
/*Version: 2.0 (using Java)*/
/*Starting date: 1/08/2017 */


public class MyVectorList{
	private MyVector root;

	public MyVectorList(){
		root = null;
	}

	public MyVector getRoot(){
		return root;
	}

	public boolean isEmpty(){
		if(root == null) return true;
		return false;
	}

	public void insert(MyVector v){ // insert following the order that y2 decreasing
		if (root == null){
			root = v;
		}
		else{
			if (v.getCoordinate()[1] + v.getCoordinate()[2] > root.getCoordinate()[1] + root.getCoordinate()[2]){
				v.setNext(root);
				root = v;
			}
			else{
				MyVector itr = root;
				if (itr.getNext() == null){
					root.setNext(v);
				}
				else{
					while (itr.getNext() != null){
						if (v.getCoordinate()[1] + v.getCoordinate()[2] >= itr.getNext().getCoordinate()[1] + itr.getNext().getCoordinate()[2]){
							v.setNext(itr.getNext());
							itr.setNext(v);
							return;
						}
						itr = itr.getNext();
					}
					itr.setNext(v);
				}
			}
		}
	}

	public void fixOverlap(double threshold){
		MyVector v = root;
		while (v != null){
			int numFace = 0;
			MyVector itr = v;
			MyVector itrNext = itr.getNext();
			while (itrNext != null){
				int x1i = v.getCoordinate()[0];
				int x1j = itrNext.getCoordinate()[0];
				int y1i = v.getCoordinate()[1];
				int y1j = itrNext.getCoordinate()[1];
				int ai = v.getCoordinate()[2];
				int aj = itrNext.getCoordinate()[2];
				int x2i = x1i + ai;
				int x2j = x1j + aj;
				int y2i = y1i + ai;
				int y2j = y1j + aj;
				// find max x1
				int xx1 = x1i > x1j ? x1i : x1j;
				// find max y1
				int yy1 = y1i > y1j ? y1i : y1j;
				// find min y1
				int xx2 = x2i < x2j ? x2i : x2j;
				// find min y2
				int yy2 = y2i < y2j ? y2i : y2j;
				// area of overlap
				int h = xx2 - xx1 + 1;
				int w = yy2 - yy1 + 1;
				if (h < 0) h = 0;
				if (w < 0) w = 0;
				int areaOverlap = h*w;
				// area overlap / area window
				double r = (double)areaOverlap / (aj*aj);
				// delete window if ratio greater than threshold
				itrNext = itrNext.getNext();
				if (r > threshold){
					itr.setNext(itrNext);
					numFace++;
				}
				else{
					itr = itr.getNext();
				}
			}
			v = v.getNext();
		}
	}

	public int size(){
		int s = 0;
		MyVector v = root;
		while(v != null){
			s = s + 1;
			v = v.getNext();
		}
		return s;
	}
}