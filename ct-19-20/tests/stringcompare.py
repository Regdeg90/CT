def compare(x,y):
    x = x.replace(" ", "")
    x = x.replace("\n", "")
    x = x.replace("\r", "")
    x = x.replace("\t", "")
    y = y.replace(" ", "")
    y = y.replace("\n", "")
    y = y.replace("\r", "")
    y = y.replace("\t", "")

    print(x)
    print(y)
    if (x == y):
        return True
    else:
        return False


