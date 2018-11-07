/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>

#include "uinput.h"
#include "suinput.h"

#define DEBUG_TAG "Synergy"


static void build_key_translation_table ();

/*
 * File descriptor for /dev/uinput
 */
int uinput_fd = 0;

/*
 * Synergy key to KEY_* translation lookup table
 */
int keyTranslation [65535];

/*
 * Start event injection
 */
void Java_org_synergy_injection_Injection_start (JNIEnv *env, jobject thiz, jstring deviceName) {
  struct input_id id = {
    0x06, /*BUS_VIRTUAL, /* Bus type. */
    1, /* Vendor id. */
    1, /* Product id. */
    1 /* Version id. */
  };

  jboolean isCopy;
  const char * szDeviceName = (*env)->GetStringUTFChars(env, deviceName, &isCopy);
  uinput_fd = suinput_open(szDeviceName, &id);

  build_key_translation_table ();
}
                                        


/*
 * Close down event injection
 */
void Java_org_synergy_injection_Injection_stop (JNIEnv *env, jobject thiz) {
  suinput_close (uinput_fd);
}


static void build_key_translation_table () {
    int i = 0;
    for (i = 0; i < 65535; i++) {
        keyTranslation [i] = -1;
    }

    keyTranslation ['a'] = KEY_A;
    keyTranslation ['b'] = KEY_B;
    keyTranslation ['c'] = KEY_C;
    keyTranslation ['d'] = KEY_D;
    keyTranslation ['e'] = KEY_E;
    keyTranslation ['f'] = KEY_F;
    keyTranslation ['g'] = KEY_G;
    keyTranslation ['h'] = KEY_H;
    keyTranslation ['i'] = KEY_I;
    keyTranslation ['j'] = KEY_J;
    keyTranslation ['k'] = KEY_K;
    keyTranslation ['l'] = KEY_L;
    keyTranslation ['m'] = KEY_M;
    keyTranslation ['n'] = KEY_N;
    keyTranslation ['o'] = KEY_O;
    keyTranslation ['p'] = KEY_P;
    keyTranslation ['q'] = KEY_Q;
    keyTranslation ['r'] = KEY_R;
    keyTranslation ['s'] = KEY_S;
    keyTranslation ['t'] = KEY_T;
    keyTranslation ['u'] = KEY_U;
    keyTranslation ['v'] = KEY_V;
    keyTranslation ['w'] = KEY_W;
    keyTranslation ['x'] = KEY_X;
    keyTranslation ['y'] = KEY_Y;
    keyTranslation ['z'] = KEY_Z;
    
    keyTranslation ['A'] = KEY_A;
    keyTranslation ['B'] = KEY_B;
    keyTranslation ['C'] = KEY_C;
    keyTranslation ['D'] = KEY_D;
    keyTranslation ['E'] = KEY_E;
    keyTranslation ['F'] = KEY_F;
    keyTranslation ['G'] = KEY_G;
    keyTranslation ['H'] = KEY_H;
    keyTranslation ['I'] = KEY_I;
    keyTranslation ['J'] = KEY_J;
    keyTranslation ['K'] = KEY_K;
    keyTranslation ['L'] = KEY_L;
    keyTranslation ['M'] = KEY_M;
    keyTranslation ['N'] = KEY_N;
    keyTranslation ['O'] = KEY_O;
    keyTranslation ['P'] = KEY_P;
    keyTranslation ['Q'] = KEY_Q;
    keyTranslation ['R'] = KEY_R;
    keyTranslation ['S'] = KEY_S;
    keyTranslation ['T'] = KEY_T;
    keyTranslation ['U'] = KEY_U;
    keyTranslation ['V'] = KEY_V;
    keyTranslation ['W'] = KEY_W;
    keyTranslation ['X'] = KEY_X;
    keyTranslation ['Y'] = KEY_Y;
    keyTranslation ['Z'] = KEY_Z;

    keyTranslation ['0'] = KEY_0;
    keyTranslation ['1'] = KEY_1;
    keyTranslation ['2'] = KEY_2;
    keyTranslation ['3'] = KEY_3;
    keyTranslation ['4'] = KEY_4;
    keyTranslation ['5'] = KEY_5;
    keyTranslation ['6'] = KEY_6;
    keyTranslation ['7'] = KEY_7;
    keyTranslation ['8'] = KEY_8;
    keyTranslation ['9'] = KEY_9;

    /**
     * this is definitely not the correct way to handle
     *  the shift key... this should really be done with
     *  the mask...
     */

	keyTranslation ['!'] = KEY_1;
	keyTranslation ['@'] = KEY_2;
	keyTranslation ['#'] = KEY_3;
	keyTranslation ['$'] = KEY_4;
	keyTranslation ['%'] = KEY_5;
	keyTranslation ['^'] = KEY_6;
	keyTranslation ['&'] = KEY_7;
	keyTranslation ['*'] = KEY_8;
	keyTranslation ['('] = KEY_9;
	keyTranslation [')'] = KEY_0;
	keyTranslation ['-'] = KEY_MINUS;
	keyTranslation ['_'] = KEY_MINUS;
	keyTranslation ['='] = KEY_EQUAL;
	keyTranslation ['+'] = KEY_EQUAL;
	keyTranslation ['?'] = KEY_SLASH;
	keyTranslation ['/'] = KEY_SLASH;
	keyTranslation ['.'] = KEY_DOT;
	keyTranslation ['>'] = KEY_DOT;
	keyTranslation [','] = KEY_COMMA;
	keyTranslation ['<'] = KEY_COMMA;


    keyTranslation [61192] = KEY_BACKSPACE;
    keyTranslation [8] = KEY_BACKSPACE;

    keyTranslation [61197] = KEY_ENTER;
    keyTranslation [10] = KEY_ENTER;
    keyTranslation [13] = KEY_ENTER;
    keyTranslation [32] = KEY_SPACE;

    // Arrows
    keyTranslation [61265] = KEY_LEFT;
    keyTranslation [61266] = KEY_UP;
    keyTranslation [61267] = KEY_RIGHT;
    keyTranslation [61268] = KEY_DOWN;

    keyTranslation [61193] = KEY_TAB;
    
    keyTranslation [61410] = KEY_RIGHTSHIFT;
    keyTranslation [61409] = KEY_LEFTSHIFT;
    
    keyTranslation [27] = KEY_BACK;      // ESC to BACK
    keyTranslation [61211] = KEY_BACK;   // ESC to BACK
    
    keyTranslation [63236] = KEY_HOMEPAGE;   // F1 to HOME
    keyTranslation [63237] = KEY_MENU;   // F2 to MENU
    keyTranslation [63238] = KEY_BACK;   // F3 to BACK
    keyTranslation [63239] = KEY_SEARCH; // F4 to SEARCH
    keyTranslation [63240] = KEY_POWER;  // F5 to POWER
    
    keyTranslation [61374] = KEY_HOMEPAGE;   // F1 to HOME
    keyTranslation [61375] = KEY_MENU;   // F2 to MENU
    keyTranslation [61376] = KEY_BACK;   // F3 to BACK
    keyTranslation [61377] = KEY_SEARCH; // F4 to SEARCH
    keyTranslation [61378] = KEY_POWER;  // F5 to POWER
}

static inline int keycode (int key) {
    if (key < 0 || key > 65535) {
        return KEY_SPACE;
    }
    
    return keyTranslation [key];
}

void Java_org_synergy_injection_Injection_keydown (JNIEnv *env, jobject thiz, jint key, jint mask) {
    int translatedKey = keycode (key);
    if (translatedKey < 0) {
        // Unknown key...
        return;
    }

    suinput_press (uinput_fd, translatedKey);
}

void Java_org_synergy_injection_Injection_keyup (JNIEnv *env, jobject thiz, jint key, jint mask) {
    int translatedKey = keycode (key);
    if (translatedKey < 0) {
        // Unknown key...
        return;
    }
    suinput_release (uinput_fd, translatedKey);
}

void Java_org_synergy_injection_Injection_movemouse (JNIEnv *env, jobject thiz, const jint x, const jint y) {
    suinput_move_pointer (uinput_fd, x, y);
}

void Java_org_synergy_injection_Injection_mousedown (JNIEnv *env, jobject thiz, jint buttonId) {
    suinput_press (uinput_fd, BTN_LEFT);
}

void Java_org_synergy_injection_Injection_mouseup (JNIEnv *env, jobject thiz, jint buttonId) {
    suinput_release (uinput_fd, BTN_LEFT);
}

void Java_org_synergy_injection_Injection_mousewheel (JNIEnv *env, jobject thiz, jint x, jint y) {
    suinput_move_wheel (uinput_fd, x, y);
}
