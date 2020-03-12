// Board layout
char a11 ; char  a12 ; char a13 ;
char a21 ; char a22 ; char a23 ;
char a31 ; char a32; char a33;
char empty ; // Empty cell character

int set(char row, int col, char mark) {
  int r;
  r = 1;
  if (row == 'a') {
     if (col == 1) {
       
        if (a11 == empty)
       
	    a11 = mark;
       
      
	else
	  r = -1;
     } else {
       if (col == 2) {
         if (a12 == empty)
           a12 = mark;
	 else
	   r = -1;
       } else {
         if (col == 3) {
	   if (a13 == empty)
              a13 = mark;
	   else
	     r = -1;
         } else {
           r = 0;
         }
       }
    }
  } else {
    if (row == 'b') {
       if (col == 1) {
          if (a21 == empty)
            a21 = mark;
	  else
	    r = -1;
       } else {
         if (col == 2) {
	    if (a22 == empty)
              a22 = mark;
	    else
	      r = -1;
         } else {
	    if (col == 3) {
	      if (a23 == empty)
                 a23 = mark;
              else
	       r = -1;
            } else {
	      r = 0;
	    }
	}
      }
    } else {
     if (row == 'c') {
        if (col == 1) {
	   if (a31 == empty)
             a31 = mark;
	   else
	     r = -1;
        } else {
           if (col == 2) {
	      if (a32 == empty)
                a32 = mark;
              else
	        r = -1;
           } else {
              if (col == 3) {
	        if (a33 == empty)
                   a33 = mark;
		else
		  r = -1;
	      } else {
	        r = 0;
	      }
          }
        }
     } else {
       r = 0;
     }
   }
  }
 print_c(a11);
 print_c('\n');
 print_c(a12);
 print_c('\n');
 print_c(a13);
 print_c('\n');
 print_c(a21);
 print_c('\n');
 print_c(a22);
 print_c('\n');
 print_c(a23);
 print_c('\n');
 print_c(a31);
 print_c('\n');
 print_c(a32);
 print_c('\n');
 print_c(a33);
 print_c('\n');

  return r;
}

void main(){
    int row;
    char column;
    char mark;

    // print_s((char*)"\nEnter the column\n");
    // column = read_c();
    // print_s((char*)"\nEnter the row\n");
    // row = read_i();
    // print_s((char*)"\nEnter the mark\n");
    // mark = read_c();
    // print_c('\n');
    
    

    set('a', 2, 'X');


}