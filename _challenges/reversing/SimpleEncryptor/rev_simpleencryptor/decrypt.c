
#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>

uint32_t decode(unsigned char buffer[4])
{
    return ((uint32_t)buffer[3] << 24) | ((uint32_t)buffer[2] << 16) | ((uint32_t)buffer[1] << 8) | (uint32_t)buffer[0];
}

void encrypt(uint32_t seed, char* file_contents, long file_size) {
	srand(seed);
	for (long i = 0; i < file_size; i = i + 1) {
		int random_v1 = rand();
		int random_v2 = rand();
		*(file_contents + i) = *(file_contents+i) ^ random_v1;
		random_v2 = random_v2 & 7;
		*(file_contents + i) =
		   *(file_contents + i) << (char)random_v2 |
			    *(file_contents + i) >> 8 - (char)random_v2;
	}
}

char encrypt_sc(char c, int r1, int r2) {
	c=c^r1;
	r2=r2&7;
	c=c << (char)r2 | r2 >> 8 - (char)r2;
	return c;
}

char decrypt(int r1, int r2, char tgt) {
	for (int i = 0; i < 256; i++) {
		char x = i;
		char y = encrypt_sc(x, r1,r2);
		if (y == tgt) {
			return x;
		}
	}
	return 'x';
}

void test_encrypt_decrypt(uint32_t seed, char* y, uint32_t sz) {
	srand(seed);
	for (int i = 0; i < sz; i++) {
		int r1 = rand();
		int r2 = rand();
		char x = decrypt(r1, r2, y[i]);
		printf("%c\n", x);
	}
}


void main() {
	FILE* fd = fopen("flag.enc", "rb");
	fseek(fd, 0, 0);

	unsigned char buffer[4];
	fread(buffer, 1, 4, fd);

	fseek(fd, 0, 2);
	uint32_t flag_sz = ftell(fd) - 4;
	fseek(fd, 4, 0);
	char* flag_buffer = malloc(flag_sz);
	fread(flag_buffer, 1, flag_sz+1, fd);
	flag_buffer[flag_sz] = '\0';

	fclose(fd);
	

	uint32_t seed = decode(buffer);

	// ========

	FILE* fw = fopen("flag", "rb");
	fseek(fw, 0, 2);
	auto fwsz = ftell(fw);
	char* fwbfr = malloc(fwsz);
	fseek(fw, 0, 0);
	fread(fwbfr, 1, fwsz, fw);


	printf("[.] seed: %lu\n", seed);

	printf("[.] pre-encryption: %s\n", fwbfr);
	encrypt(seed, fwbfr, fwsz);
	printf("[.] post-encryption: %s\n", fwbfr);
	printf("[.] flag-encrypted: %s\n", flag_buffer);

	srand(seed);
	printf("[.] raw: %c | encrypt_sc: %c\n", fwbfr[0], encrypt_sc('f', rand(), rand()));


	fw = fopen("flag.enc.2", "wb");
	fwrite(fwbfr, 1, fwsz, fw);

	// test_encrypt_decrypt(seed, fwbfr, fwsz);


}

