package com.gentle.bank.customer.util;

import java.util.Random;

/**
 * A utility class that provides various ASCII art representations for use in application banners or logs.
 * <p>
 * This class includes different styles of ASCII art, known as Figlets, which can be used to generate
 * decorative text-based banners for visual purposes. The available Figlets are:
 * <ul>
 *   <li>{@link #IVRIT} - A stylized representation of text in a classic font.</li>
 *   <li>{@link #BIG} - A bold and large font style for text representation.</li>
 *   <li>{@link #SLANT} - A slanted, italicized font style.</li>
 *   <li>{@link #DOOM} - A dramatic and eye-catching font style.</li>
 *   <li>{@link #GHOST} - A whimsical and playful font style.</li>
 * </ul>
 * </p>
 * <p>
 * The Figlets are generated randomly from a predefined set of styles. This class can be used to
 * add a unique and engaging touch to the application output.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
public class Figlets {

  private static final String IVRIT = """
                    _                              ____   ___ ____  _  _    ___   ___   ____  _  _  \s
      ___ _   _ ___| |_ ___  _ __ ___   ___ _ __  |___ \\ / _ \\___ \\| || |  / _ \\ ( _ ) |___ \\| || | \s
     / __| | | / __| __/ _ \\| '_ ` _ \\ / _ \\ '__|   __) | | | |__) | || |_| | | |/ _ \\   __) | || |_\s
    | (__| |_| \\__ \\ || (_) | | | | | |  __/ |     / __/| |_| / __/|__   _| |_| | (_) | / __/|__   _|
     \\___|\\__,_|___/\\__\\___/|_| |_| |_|\\___|_|    |_____|\\___/_____|  |_|(_)___/ \\___(_)_____|  |_| \s
                                                                                                    \s""";

  private static final String BIG = """
                    _                              ___   ___ ___  _  _    ___   ___   ___  _  _  \s
                   | |                            |__ \\ / _ \\__ \\| || |  / _ \\ / _ \\ |__ \\| || | \s
      ___ _   _ ___| |_ ___  _ __ ___   ___ _ __     ) | | | | ) | || |_| | | | (_) |   ) | || |_\s
     / __| | | / __| __/ _ \\| '_ ` _ \\ / _ \\ '__|   / /| | | |/ /|__   _| | | |> _ <   / /|__   _|
    | (__| |_| \\__ \\ || (_) | | | | | |  __/ |     / /_| |_| / /_   | |_| |_| | (_) | / /_   | | \s
     \\___|\\__,_|___/\\__\\___/|_| |_| |_|\\___|_|    |____|\\___/____|  |_(_)\\___/ \\___(_)____|  |_| \s
                                                                                                 \s
                                                                                                 \s""";

  private static final String SLANT = """
                       __                               ___   ____ ___  __ __   ____  ____   ___  __ __
      _______  _______/ /_____  ____ ___  ___  _____   |__ \\ / __ \\__ \\/ // /  / __ \\( __ ) |__ \\/ // /
     / ___/ / / / ___/ __/ __ \\/ __ `__ \\/ _ \\/ ___/   __/ // / / /_/ / // /_ / / / / __  | __/ / // /_
    / /__/ /_/ (__  ) /_/ /_/ / / / / / /  __/ /      / __// /_/ / __/__  __// /_/ / /_/ / / __/__  __/
    \\___/\\__,_/____/\\__/\\____/_/ /_/ /_/\\___/_/      /____/\\____/____/ /_/ (_)____/\\____(_)____/ /_/  \s
                                                                                                      \s""";

  private static final String DOOM = """
                    _                              _____  _____  _____   ___  _____ _____   _____   ___\s
                   | |                            / __  \\|  _  |/ __  \\ /   ||  _  |  _  | / __  \\ /   |
      ___ _   _ ___| |_ ___  _ __ ___   ___ _ __  `' / /'| |/' |`' / /'/ /| || |/' |\\ V /  `' / /'/ /| |
     / __| | | / __| __/ _ \\| '_ ` _ \\ / _ \\ '__|   / /  |  /| |  / / / /_| ||  /| |/ _ \\    / / / /_| |
    | (__| |_| \\__ \\ || (_) | | | | | |  __/ |    ./ /___\\ |_/ /./ /__\\___  |\\ |_/ / |_| |_./ /__\\___  |
     \\___|\\__,_|___/\\__\\___/|_| |_| |_|\\___|_|    \\_____/ \\___/ \\_____/   |_(_)___/\\_____(_)_____/   |_/
                                                                                                       \s
                                                                                                       \s""";

  private static final String GHOST = """
                            .-')   .-') _                _   .-')      ('-.  _  .-')                                                                                            \s
                           ( OO ).(  OO) )              ( '.( OO )_  _(  OO)( \\( -O )                                                                                           \s
       .-----.,--. ,--.   (_)---\\_)     '._  .-'),-----. ,--.   ,--.|,------.,------.        .-----.   .----.   .-----.     .---.      .----.    .-----.     .-----.     .---.  \s
      '  .--./|  | |  |   /    _ ||'--...__)( OO'  .-.  '|   `.'   | |  .---'|   /`. '      / ,-.   \\ /  ..  \\ / ,-.   \\   / .  |     /  ..  \\  /  .-.  \\   / ,-.   \\   / .  |  \s
      |  |('-.|  | | .-') \\  :` `.'--.  .--'/   |  | |  ||         | |  |    |  /  | |      '-'  |  |.  /  \\  .'-'  |  |  / /|  |    .  /  \\  .|   \\_.' /   '-'  |  |  / /|  |  \s
     /_) |OO  )  |_|( OO ) '..`''.)  |  |   \\_) |  |\\|  ||  |'.'|  |(|  '--. |  |_.' |         .'  / |  |  '  |   .'  /  / / |  |_   |  |  '  | /  .-. '.      .'  /  / / |  |_ \s
     ||  |`-'||  | | `-' /.-._)   \\  |  |     \\ |  | |  ||  |   |  | |  .--' |  .  '.'       .'  /__ '  \\  /  ' .'  /__ /  '-'    |  '  \\  /  '|  |   |  |   .'  /__ /  '-'    |\s
    (_'  '--'('  '-'(_.-' \\       /  |  |      `'  '-'  '|  |   |  | |  `---.|  |\\  \\       |       | \\  `'  / |       |`----|  |-'.-.\\  `'  /  \\  '-'  /.-.|       |`----|  |-'\s
       `-----' `-----'     `-----'   `--'        `-----' `--'   `--' `------'`--' '--'      `-------'  `---''  `-------'     `--'  `-' `---''    `----'' `-'`-------'     `--'  \s
                                                                                                       \s
                                                                                                       \s""";

  private static final Random RANDOM = new Random();

  /**
   * Generates a random Figlet representation.
   * <p>
   * This method randomly selects one of the predefined Figlet styles and returns it as a string.
   * The styles are defined as static final strings in the class.
   * </p>
   *
   * @return A randomly selected Figlet representation as a string.
   * @throws IllegalStateException If an unexpected value is encountered during random selection.
   */
  public String randomFigletGenerator() {
    // Generates a random number between 0 and 4 (inclusive)
    int choice = RANDOM.nextInt(5);
    // Selects the corresponding Figlet drawing based on the random number
    switch (choice) {
      case 0:
        return IVRIT;
      case 1:
        return BIG;
      case 2:
        return SLANT;
      case 3:
        return DOOM;
      case 4:
        return GHOST;
      default:
        throw new IllegalStateException("Unexpected value: " + choice);
    }
  }
}
