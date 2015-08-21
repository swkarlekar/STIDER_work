#include <iostream>
#include <string>
#include <sstream>
using namespace std;
int sum1 = 0;
int sum2 = 0;

int main()
{
	string myanswer;
	int age = 0;
	bool correctyear = false; 
	cout << "\nPlease input two numbers to add:\n";
	cin >> sum1;
	cin >> sum2;
	cout << sum1 << "+" << sum2 << "=" << sum1+sum2 << "\n";
	cout << "What is your name? ";
	getline(cin, myanswer); //dummy call
	getline(cin, myanswer);
	cout << "Welcome " << myanswer << ".\n";
	cout << "How old are you?\n";
	getline(cin, myanswer);
	stringstream(myanswer) >> age;
	cout << "Has your birthday already passed this year? ";
	getline(cin, myanswer);
	if(myanswer == "yes")
		cout << "You were born in " << 2015-age << ".\n";
	else
		cout << "You were born in " << 2015-age-1 << ".\n";

	return 0;
}