# Task 01
## Валидация на IPv4 адрес 🌐

### Условие

*IP* (*Internet Protocol*) е протокол на мрежовия слой в [OSI модела](https://en.wikipedia.org/wiki/OSI_model) и е в основата на целия Интернет. Различните устройства в мрежата се характеризират с различни IP адреси, благодарение на които те се идентифицират и могат да си комуникират. IP адресите биват два вида: [IPv4](https://en.wikipedia.org/wiki/Internet_Protocol_version_4) и [IPv6](https://en.wikipedia.org/wiki/IPv6). Днес ще се фокусираме върху IPv4 адресите. Един валиден IPv4 адрес има следния формат: `x1.x2.x3.x4`, където `xi` се нарича *октет*, `0 <= xi <= 255` и `xi`не съдържа водещи нули.

Създайте публичен клас `IPValidator` с метод

```java
public static boolean validateIPv4Address(String str)
```

който по даден низ `str` връща `true`, ако `str` e валиден IPv4 адрес, и `false` иначе. 

### Примери

| Извикване                             | Резултат |
| :------------------------------------ | :------- |
| `validateIPv4Address("192.168.1.1")`  | `true`   |
| `validateIPv4Address("192.168.1.0")`  | `true`   |
| `validateIPv4Address("192.168.1.00")` | `false`  |
| `validateIPv4Address("192.168@1.1")`  | `false`  |

### :warning: Забележки

- Използването на структури от данни, различни от масив, **не е позволено**. Задачата трябва да се реши с помощта на знанията от първата лекция от курса.

# Task 02

## Скок-подскок :runner:

### Условие

Играта `скок-подскок` се играе по следния начин: на земята имате нарисуванa последователност от квадратчета (с дължина поне едно) с неотрицателни числа в тях. Стъпвате на първото квадратче и можете да минете толкова квадратчета напред, колко е числото, върху което в момента сте стъпили. Например, ако пише числото 3, можете да направите 0, 1, 2 или 3 крачки напред. Печелите играта, ако успеете след достатъчен брой ходове от този вид да стъпите на последното квадратче.

Създайте публичен клас `JumpGame` с метод

```java
public static boolean canWin(int[] array)
```

който по даден масив `array` определя дали можете да спечелите.

### Примери

<img src="https://github.com/fmi/java-course/blob/master/01-intro-to-java/lecture/images/lab01-jump-game.jpg" alt="Jump Game">

| Извикване                          | Резултат |
| :--------------------------------- | :------- |
| `canWin(new int[]{2, 3, 1, 1, 0})` | `true`   |
| `canWin(new int[]{3, 2, 1, 0, 0})` | `false`  |

### :warning: Забележки

- Използването на структури от данни, различни от масив, **не е позволено**. Задачата трябва да се реши с помощта на знанията от първата лекция от курса.

# Task 03

## Счупената клавиатура ⌨️ ❌

### Условие

Сашко си купил нова механична клавиатура, но, за съжаление, още на първия ден от използването ѝ, разлял вода върху нея.

Оказало се, че след инцидента някои клавиши от клавиатурата не работят. 

На Сашко му се налага да изпрати важно съобщение до приятел, но не може да напише някои думи.

Създайте публичен клас `BrokenKeyboard` с метод:

```java
public static int calculateFullyTypedWords(String message, String brokenKeys);
```

който за всяко изречение (```message```), което Сашко иска да изпрати, пресмята колко от думите на съобщението могат да се напишат, без да се използват изредените счупени клавиши(```brokenKeys```).

### !!! ВАЖНО
- За дума приемаме последователност от символи (n > 0), като разделител в съобщението е интервал (`' '`) 
- Табулациите не се считат за думи
- За улеснение ще приемем, че клавиатурата съдържа само малки латински букви, цифри и специални символи

### Примери

| Извикване                                                             | Резултат |
| :-------------------------------------------------------------------- | :------- |
| `calculateFullyTypedWords("i love mjt", "qsf3o")`                     | `2`      |
| `calculateFullyTypedWords("secret      message info      ", "sms")`   | `1`      |
| `calculateFullyTypedWords("dve po 2 4isto novi beli kecove", "o2sf")` | `2`      |
| `calculateFullyTypedWords("     ", "asd")`                            | `0`      |
| `calculateFullyTypedWords(" - 1 @ - 4", "s")`                         | `5`      |

### :warning: Забележки

- Използването на структури от данни, различни от масив, **не е позволено**. Задачата трябва да се реши с помощта на знанията от първата лекция от курса.
