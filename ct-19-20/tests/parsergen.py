import random
import string




def genparser():
    x = random.randint(0,1)

    if (x==1):
        parseinc()

    x = random.randint(0,1)
    if (x==1):
        parseStruct()

    x = random.randint(0,1)
    if (x==1):
        parseVardecl()

    x = random.randint(0,1)
    if (x==1):
        parseFundecl()

def parseinc():
    f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  #include \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ") 
    f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  \"" +''.join([random.choice(string.ascii_letters + string.digits) for n in range(32)])+ "\" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  \n")
    x = random.randint(0,1)
    if (x==1):
        parseinc()

def parseStruct():
    f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  struct \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ident\n \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   {  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  \n")
    parsevardeclplus()
    f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  }\n \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ;\n \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    x = random.randint(0,1)
    if (x==1):
        parseStruct()

def parsevardeclplus():
    parseType()
    f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ident \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    x = random.randint(0,1)
    if (x==1):
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  [  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  5  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ] \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ;\n \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    parseVardecl()

def parseVardecl():
    x = random.randint(0,1)
    if (x==1):
        parseType()
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ident \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        x = random.randint(0,1)
        if (x==1):
            f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  [ \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   5  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ] \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ;\n \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        x = random.randint(0,1)
        if (x==1):
            parseVardecl()


def parseType():
    x = random.randint(1,4)
    if (x==1):
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  char  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    if (x==2):
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   int  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    if (x==3):
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   void  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    if (x==4):
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  struct \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ident \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ")
    x = random.randint(0,1)
    if (x==1):
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   * \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ")

def parseFundecl():
    parseType()
    f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ident_funcdel  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ( \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  \n")
    x = random.randint(0,1)
    if (x==1):
        parseParams()
    f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ) \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  \n")
    parseBlock()

def parseParams():
    parseType()
    f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ident_params  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ")
    x = random.randint(0,1)
    if (x==1):
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  , \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        parseParams()

def parseStmt():
    x = random.randint(1,5)
    if (x==1):
        parseBlock()
    elif (x==2):
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   while  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  (  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        parseExp()
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ) \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   \n")
        parseStmt()
    elif (x==3):
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   if \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ( \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ")
        parseExp()
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  )  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  \n")
        parseStmt()
        x = random.randint(0,1)
        if (x==1):
            f.write( " \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  else \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   \n")
            parseStmt()
    elif (x==4):
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  return  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        x = random.randint(0,1)
        if (x==1):
            parseExp()
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ; \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    elif (x==5):
        parseExp()
        x = random.randint(0,1)
        if (x==1):
            f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  =  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
            parseExp()
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ;\n \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")

def parseBlock():
    f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  \n { \n \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    x = random.randint(0,1)
    if (x==1):
        parseVardecl()
    x = random.randint(0,1)
    if (x==1):
        parseStmtstar()
    f.write("\n \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   }  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  \n")

def parseStmtstar():
    parseStmt()
    parseStmt()
    x = random.randint(0,1)
    if (x==1):
        parseStmtstar()

def parseExp():
    x  = random.randint(0,12)
    if (x==0):
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ( \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ")
        parseExp()
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   )  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  \n")
    elif (x==1):
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   exp_ident  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    elif (x==2):
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   5 \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ")
    elif (x==3):
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  \'c\'  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    elif (x==4):
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   \"string ident\" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ")
    elif (x==5):
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   -  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        parseExp()
    elif (x==6):
        parseExp()
        t = [" > ", " < ", ">=", "<=", "!=", "==", "+", "-", "/", "*", "%", "||", "&&"]
        x  = random.randint(0,12)
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        f.write(t[x])
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t " )
        parseExp()
    elif (x==7):
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  funcall_ident \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   (  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        x = random.randint(0,1)
        if (x==1):
            parseExprec()
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ) \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   \n")
    elif (x==8):
        parseExp()
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   [ \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ")
        parseExp()
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ]  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    elif (x==9):
        parseExp()
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  .  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  fieldaccess_ident \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    elif (x==10):
        f.write ("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  *  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        parseExp()
    elif (x==11):
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  sizeof  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ( \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        parseType()
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  )  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
    elif (x==12):
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   (  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        parseType()
        f.write(" \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ) \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t   ")
        parseExp()

def parseExprec():
    parseExp()
    x = random.randint(0,1)
    if (x==1):
        f.write("  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ,  \n /* multilinecome \n \n */ \n \n //single line comment \n \n \t  ")
        parseExprec()











for i in range(0,2000):
    f= open("test"+ str(i) + ".c","w+")
    genparser()
    f.close()
    if (i%10000==0):
        print(i)
