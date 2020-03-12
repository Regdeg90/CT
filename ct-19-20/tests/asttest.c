struct s {
    int s;
};
struct t {
    int s;
    struct s y;
};
struct p {
    struct t t;
};
int s;
struct p z;

void main(){
    int y;
    
    z.t.y.s = 5;

    y = z.t.y.s;

    print_i(z.t.y.s);
    print_i(y);

    

}
// int auto_return() {
// 	return 1;
// }

// int mar() {
// 	if (1) {
// 		auto_return();
// 	}
// }
// // struct x{
// //     int a; 
// // }; 

// int mai(){
//     void a[4];
//     // struct s s;
//     return 0;
// }
// void m() {
//     (char) 2;
// }