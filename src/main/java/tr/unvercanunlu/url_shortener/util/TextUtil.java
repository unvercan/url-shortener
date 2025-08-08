package tr.unvercanunlu.url_shortener.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextUtil {

  public static String randomTextGenerate(int minLength, int maxLength, boolean containsDuplicate, boolean containsDigit, boolean containsUpperCase) {
    Random random = ThreadLocalRandom.current();

    Collection<Character> characters;
    if (containsDuplicate) {
      characters = new ArrayList<>();
    } else {
      characters = new HashSet<>();
    }

    int length = random.nextInt(minLength, (maxLength + 1));

    while (characters.size() < length) {
      boolean isCharacter = random.nextBoolean();
      boolean isUppercase = isCharacter && random.nextBoolean();

      Character generated = null;
      if (isCharacter) {
        if (isUppercase && containsUpperCase) {
          generated = (char) ('A' + random.nextInt(('Z' - 'A') + 1));
        } else {
          generated = (char) ('a' + random.nextInt(('z' - 'a') + 1));
        }
      } else if (containsDigit) {
        generated = (char) (random.nextInt(0, 10) + '0');
      }

      if (generated != null) {
        characters.add(generated);
      }
    }

    List<Character> list = new ArrayList<>(characters);
    Collections.shuffle(list);

    StringBuilder builder = new StringBuilder();
    list.forEach(builder::append);

    return builder.toString();
  }

}
