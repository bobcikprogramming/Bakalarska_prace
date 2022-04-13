package com.bobcikprogramming.kryptoevidence.Controller;

// https://stackoverflow.com/a/13079815

/**
 * Delegátor využívaný u asynchronních operací API a Firebase
 *
 * Inspirován z:
 * Zdroj:   Stack Overflow
 * Dotaz:   https://stackoverflow.com/q/13079645
 * Odpověď: https://stackoverflow.com/a/13079815
 * Autor:   RookieWen
 * Autor:   https://stackoverflow.com/users/1775938/rookiewen
 * Datum:   26. října 2012
 */
public interface TaskDelegate {
    void TaskCompletionResult(String result);
}
