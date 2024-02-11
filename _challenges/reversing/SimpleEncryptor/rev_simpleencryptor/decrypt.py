
"""
srand(current_time);
for (i = 0; i < (long)file_size; i = i + 1) {
    random_v1 = rand();
    *(byte *)((long)file_contents + i) = *(byte *)((long)file_contents + i) ^ (byte)random_v1;
    random_v2 = rand();
    random_v2 = random_v2 & 7;
    x = x << random_v2 | x >> 8 - random_v2;
}
"""

import random
import sys
# random.seed(1337)
# x = "a".encode('utf-8')[0]


def get_random_seed() -> int:
    with open("flag.enc", "rb") as f:
        return int.from_bytes(f.read(4), byteorder='little')


def _r(): return random.randint(0, 2147483647)
def _rs(): random.seed(get_random_seed())


def encrypt(x, r1, r2):
    _x = x^r1
    r2 = r2&7
    y = (_x<<r2)|(_x>>(8-r2))
    print(f"{x=}", f"{_x=}", f"{r1=}", f"{r2=}", f"{y=}")
    return y

def gen_inverse_table(r1, r2):
    t = {}
    for x in range(256):
        t[encrypt(x, r1,r2)] = x
    return t

def decrypt(x):
    r1, r2 = _r(), _r()
    t = gen_inverse_table(r1, r2)
    return t[x]

_rs()
y = encrypt(int(sys.argv[1]), _r(), _r())
_rs()
x = decrypt(y)

print(f"{y=}, {x=}")


sys.exit(0)


random.seed(get_random_seed())
with open("flag.enc", "rb") as f:
    f.seek(5, 0)
    y = f.read()
    for yi in y:
        r = _r()
        t = gen_inverse_table(_r() & 7)
        x = t[yi^r]
        print(f"{x}", end="")

        






