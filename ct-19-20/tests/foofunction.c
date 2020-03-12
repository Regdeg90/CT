int y;

// void bar(int p) {
//     print_i(p);
// }

int foo(int x) {
    // print_i(x);
    // print_s((char*)"\n");
    // print_i(p);
    // print_s((char*)"\n");
    // print_i(r);
    // print_s((char*)"\n");
    // print_i(d);
    // print_s((char*)"\n");
    // print_c(s);
    // print_s((char*)"\n");
    // // print_i(y);
    // // bar(5);
    // if (s == 'A') {
    //     print_s((char*)"Its a match\n");
    // }
    // if (x==3) {
    //     print_s((char*)"It's another match\n")
    //     return x;
    // } else {
    //     return 7;
    // }
    print_s((char*)"\n This is X");
    print_i(x);
    print_c('\n');
    print_s((char*)"\n Got here");
    return x +1;
    
}


void main(){
    // int e;
    // int y2;
    // e = 7;
    y = foo(9-1);
    y = foo(y);
    print_i(y);
    print_s((char*)"\n This is y");
    print_i(foo(foo(9)));

    // print_c('\n');
    // print_i(y +2);
    // e = 4;
    // print_i(e);
}