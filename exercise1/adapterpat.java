// MediaPlayer Interface
interface MediaPlayer {
    void play(String audioType, String fileName);
}

// AdvancedMediaPlayer Interface
interface AdvancedMediaPlayer {
    void playMP4(String fileName);
    void playMP3(String fileName);
}

// Concrete Advanced Media Player
class MP4Player implements AdvancedMediaPlayer {
    public void playMP4(String fileName) {
        System.out.println("Playing MP4 file: " + fileName);
    }

    public void playMP3(String fileName) {
        // do nothing
    }
}

class MP3Player implements AdvancedMediaPlayer {
    public void playMP3(String fileName) {
        System.out.println("Playing MP3 file: " + fileName);
    }

    public void playMP4(String fileName) {
        // do nothing
    }
}

// Adapter
class MediaAdapter implements MediaPlayer {
    AdvancedMediaPlayer advancedMediaPlayer;

    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("MP4")) {
            advancedMediaPlayer = new MP4Player();
        } else if (audioType.equalsIgnoreCase("MP3")) {
            advancedMediaPlayer = new MP3Player();
        }
    }

    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("MP4")) {
            advancedMediaPlayer.playMP4(fileName);
        } else if (audioType.equalsIgnoreCase("MP3")) {
            advancedMediaPlayer.playMP3(fileName);
        }
    }
}

// Concrete Media Player
class AudioPlayer implements MediaPlayer {
    MediaAdapter mediaAdapter;

    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("MP3")) {
            System.out.println("Playing MP3 file: " + fileName);
        } else if (audioType.equalsIgnoreCase("MP4")) {
            mediaAdapter = new MediaAdapter(audioType);
            mediaAdapter.play(audioType, fileName);
        } else {
            System.out.println("Invalid media. " + audioType + " format not supported");
        }
    }
}

// Main
public class AdapterPatternDemo {
    public static void main(String[] args) {
        AudioPlayer audioPlayer = new AudioPlayer();

        audioPlayer.play("MP3", "song.mp3");
        audioPlayer.play("MP4", "video.mp4");
    }
}
