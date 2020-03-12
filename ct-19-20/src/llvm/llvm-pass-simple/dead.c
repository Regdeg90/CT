// int foo() {
//   int a = 7;
//   int b = a * 2;
//   int c = b - a;   // dead 
//   int d = c / a;   // dead
//   return b;
// }
int sum (int a , int b)
{
  int i;
  int res = 1;
  for (i =a; i<b; i++)
  {
    res *= i;
  }
  return res;
}
