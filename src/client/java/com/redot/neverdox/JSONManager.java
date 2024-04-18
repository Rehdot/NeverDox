package com.redot.neverdox;

import com.google.gson.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONManager {

    public static final String FILE_NAME = NeverDox.minecraftDir + "/NeverDoxConfig.json";
    public static File neverDoxConfig = createJSONFileIfNotExists();

    public static File createJSONFileIfNotExists() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            JsonObject jsonObject = new JsonObject();
            JsonArray phrasesArray = new JsonArray();

            jsonObject.addProperty("webhook", "Paste your webhook here!");
            jsonObject.addProperty("dispatch", 0);
            jsonObject.add("phrases", phrasesArray);

            try (FileWriter writer = new FileWriter(FILE_NAME)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(jsonObject, writer);
                System.out.println("[NeverDox] Created new JSON file: " + FILE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    public static int incrementDispatchNumber() {
        int dispatchNumber = 0;

        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            dispatchNumber = jsonObject.getAsJsonPrimitive("dispatch").getAsInt();
            jsonObject.addProperty("dispatch", dispatchNumber + 1);

            try (FileWriter writer = new FileWriter(FILE_NAME)) {
                gson.toJson(jsonObject, writer);
            } catch (IOException ignored) {}

        } catch (IOException ignored) {}

        return dispatchNumber + 1;
    }

    public static void addPhraseToJSON(String text, boolean exempt, boolean ping) {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray phrasesArray = jsonObject.getAsJsonArray("phrases");

            if (JSONManager.phraseExists(text)) {
                MSGManager.sendCheckupMessage("Instance of '" + text + "' is already stored!");
                return;
            }

            JsonObject phraseObject = new JsonObject();
            phraseObject.addProperty("text", text.toLowerCase());
            phraseObject.addProperty("exempt", exempt);
            phraseObject.addProperty("pings", ping);
            phrasesArray.add(phraseObject);

            try (FileWriter writer = new FileWriter(FILE_NAME)) {
                gson.toJson(jsonObject, writer);
                String tempStr = "";
                if (exempt) tempStr += "exemption ";
                if (ping) tempStr += "ping ";
                MSGManager.sendCheckupMessage("Added " + tempStr + "'" + text + "' to the NeverDox directory.");
            } catch (IOException ignored) {
                MSGManager.sendCheckupMessage("Failed to add phrase to NeverDox directory.");
            }
        } catch (IOException ignored) {
            MSGManager.sendCheckupMessage("Failed to read NeverDox directory.");
        }
    }

    public static String getWebhook() {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            return jsonObject.get("webhook").getAsString();
        } catch (IOException e) {
            MSGManager.sendCheckupMessage("Could not find discord webhook. Use '?nd open' to enter a webhook!");
        }
        return null;
    }

    public static PhraseDetails getPhrase(String text) {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray phrasesArray = jsonObject.getAsJsonArray("phrases");

            for (JsonElement element : phrasesArray) {
                JsonObject phraseObject = element.getAsJsonObject();
                String phraseText = phraseObject.get("text").getAsString();
                if (phraseText.equals(text)) {
                    boolean exempt = phraseObject.get("exempt").getAsBoolean();
                    boolean ping = phraseObject.get("pings").getAsBoolean();
                    return new PhraseDetails(text, exempt, ping);
                }
            }
        } catch (IOException e) {
            MSGManager.sendCheckupMessage("Instance of '" + text + "' not found in NeverDox directory.");
        }
        return null;
    }

    public static List<String> getAllPhrases() {
        List<String> allPhrases = new ArrayList<>();
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray phrasesArray = jsonObject.getAsJsonArray("phrases");

            for (JsonElement element : phrasesArray) {
                JsonObject phraseObject = element.getAsJsonObject();
                String phraseText = phraseObject.get("text").getAsString();
                allPhrases.add(phraseText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allPhrases;
    }

    public static List<PhraseDetails> getAllFullPhrases() {
        List<PhraseDetails> allPhrases = new ArrayList<>();
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray phrasesArray = jsonObject.getAsJsonArray("phrases");

            for (JsonElement element : phrasesArray) {
                PhraseDetails details = gson.fromJson(element, PhraseDetails.class);
                allPhrases.add(details);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allPhrases;
    }

    public static List<PhraseDetails> getAllExemptPhrases() {
        List<PhraseDetails> exemptPhrases = new ArrayList<>();

        for (PhraseDetails phraseDetail : getAllFullPhrases()) {
            if (phraseDetail.isExempt()) exemptPhrases.add(phraseDetail);
        }

        return exemptPhrases;
    }

    public static void removePhrase(String text) {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray phrasesArray = jsonObject.getAsJsonArray("phrases");

            if (!phraseExists(text)) {
                MSGManager.sendCheckupMessage("Instance of '" + text + "' not found in NeverDox directory.");
                return;
            }

            Iterator<JsonElement> iterator = phrasesArray.iterator();
            while (iterator.hasNext()) {
                JsonElement element = iterator.next();
                JsonObject phraseObject = element.getAsJsonObject();
                String phraseText = phraseObject.get("text").getAsString();
                if (phraseText.equalsIgnoreCase(text)) {
                    iterator.remove();
                    break;
                }
            }

            try (FileWriter writer = new FileWriter(FILE_NAME)) {
                gson.toJson(jsonObject, writer);
                MSGManager.sendCheckupMessage("Removed '" + text + "' from NeverDox directory.");
            } catch (IOException ignored) {}
        } catch (IOException ignored) {
            MSGManager.sendCheckupMessage("Failed to read NeverDox directory.");
        }
    }

    public static List<PhraseDetails> findMatchingPhrases(String lowerCaseMsg) {
        List<PhraseDetails> matches = new ArrayList<>();
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray phrasesArray = jsonObject.getAsJsonArray("phrases");

            for (JsonElement element : phrasesArray) {
                PhraseDetails details = gson.fromJson(element, PhraseDetails.class);
                if (lowerCaseMsg.contains(details.getText().toLowerCase())) {
                    matches.add(details);
                }
            }
            return matches;

        } catch (IOException e) {
            MSGManager.sendCheckupMessage("Could not read NeverDox directory.");
        }
        return null;
    }

    public static boolean phraseExists(String text) {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray phrasesArray = jsonObject.getAsJsonArray("phrases");

            for (JsonElement element : phrasesArray) {
                JsonObject phraseObject = element.getAsJsonObject();
                String phraseText = phraseObject.get("text").getAsString();
                if (phraseText.equalsIgnoreCase(text)) {
                    return true;
                }
            }
        } catch (IOException e) {
            MSGManager.sendCheckupMessage("Could not read NeverDox directory.");
        }
        return false;
    }

    public static class PhraseDetails {
        private final String text;
        private final boolean exempt;
        private final boolean pings;

        public PhraseDetails(String text, boolean exempt, boolean pings) {
            this.text = text;
            this.exempt = exempt;
            this.pings = pings;
        }

        public String getText() {
            return text;
        }

        public boolean isExempt() {
            return exempt;
        }

        public boolean getPings() {
            return pings;
        }
    }
}
