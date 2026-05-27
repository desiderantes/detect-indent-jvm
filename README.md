# detect-indent

> Detect the indentation of code

A simple library to detect the indentation of a file. This is a Java port of the popular [detect-indent](https://github.com/sindresorhus/detect-indent) JavaScript library.

## Installation

### Gradle

```kotlin
dependencies {
    implementation("com.desiderantes:detect-indent:1.0.0")
}
```

### Maven

```xml
<dependency>
    <groupId>com.desiderantes</groupId>
    <artifactId>detect-indent</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

```java
import com.desiderantes.detectindent.IndentDetector;
import com.desiderantes.detectindent.Indent;

public class Main {
    public static void main(String[] args) {
        String code = "{\n    \"key\": \"value\"\n}";
        Indent indent = IndentDetector.detectIndent(code);

        if (indent != null) {
            System.out.println("Amount: " + indent.amount());
            //=> Amount: 4
            System.out.println("Type: " + indent.type());
            //=> Type: SPACE
            System.out.println("Indent string: '" + indent.indent() + "'");
            //=> Indent string: '    '
        }
    }
}
```

## API

### `IndentDetector.detectIndent(String)`

Detects the indentation type and amount in a given string.

Returns an `Indent` record or `null` if no indentation was detected.

### `Indent` Record

- `type()`: Returns `IndentType` (`SPACE` or `TAB`).
- `amount()`: Returns the number of spaces or tabs used for indentation.
- `indent()`: Returns the actual indentation string.

## License

MIT © [sindresorhus](https://sindresorhus.com) and [desiderantes](https://github.com/desiderantes)
