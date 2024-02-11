
#include <stdio.h>

void main() {
	int random_v2 = rand() & 7;
	printf("%d\n", random_v2);

	auto a = (byte)"a";
	a  = a << random_v2 | a >> 8 - random_v2;
	printf("%d\n", a);
	a = a >> random_v2 & a << 8 - random_v2;

	printf("%d\n", a);
}
