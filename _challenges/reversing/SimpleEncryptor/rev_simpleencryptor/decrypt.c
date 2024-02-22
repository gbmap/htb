
#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <string.h>

void main() {
	FILE* fd = fopen("flag.enc.bp", "rb");
	fseek(fd, 0, 0);

	int seed;
	fread(&seed, sizeof(seed), 1, fd);

	fseek(fd, 0, 2);
	uint32_t flag_sz = ftell(fd) - 4;
	fseek(fd, 4, 0);
	char* flag_buffer = malloc(flag_sz);
	fread(flag_buffer, 1, flag_sz, fd);


	srand(seed);
	for (int i = 0; i < flag_sz; i++) {
		int rand1 = rand();
		int rand2 = rand()&7;

		flag_buffer[i] = ( (unsigned char)flag_buffer[i]>> (rand2))|(flag_buffer[i]<<(8-rand2));
		flag_buffer[i] = rand1 ^ flag_buffer[i];
		printf("%c", flag_buffer[i]);
	}
	




}

