/*
 * Copyright 2014 MovingBlocks
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
 */
package org.terasology.journal.ui;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.journal.JournalManager;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.layouts.ScrollableArea;
import org.terasology.rendering.nui.widgets.ItemActivateEventListener;
import org.terasology.rendering.nui.widgets.UIList;
import org.terasology.rendering.nui.widgets.browser.data.DocumentData;
import org.terasology.rendering.nui.widgets.browser.ui.BrowserWidget;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
public class JournalNUIWindow extends CoreScreenLayer {
    private BrowserWidget journalList;
    private UIList<JournalManager.JournalChapter> chapterList;

    private JournalChapterRenderer chapterRenderer = new JournalChapterRenderer();

    private JournalManager.JournalChapter selectedChapter;

    @Override
    public void initialise() {
        final ScrollableArea scrollArea = find("entries", ScrollableArea.class);
        scrollArea.moveToBottom();

        journalList = find("journalList", BrowserWidget.class);

        chapterList = (UIList<JournalManager.JournalChapter>) find("chapterList", UIList.class);

        chapterList.setItemRenderer(chapterRenderer);
        chapterList.subscribe(
                new ItemActivateEventListener<JournalManager.JournalChapter>() {
                    @Override
                    public void onItemActivated(UIWidget widget, JournalManager.JournalChapter item) {
                        updateJournal();
                    }
                });
    }

    public void refreshJournal() {
        selectedChapter = null;
        updateChapters();
        List<JournalManager.JournalChapter> chapters = chapterList.getList();
        if (chapters.size() > 0) {
            chapterList.setSelection(chapters.get(0));
        }
    }

    @Override
    public void update(float delta) {
        JournalManager.JournalChapter selection = chapterList.getSelection();
        if (selection != selectedChapter) {
            selectedChapter = selection;
            updateJournal();
        }
    }

    private void updateChapters() {
        JournalManager journalManager = CoreRegistry.get(JournalManager.class);
        EntityRef playerEntity = CoreRegistry.get(LocalPlayer.class).getCharacterEntity();
        Map<JournalManager.JournalChapter, DocumentData> playerEntries = journalManager.getPlayerEntries(playerEntity);

        List<JournalManager.JournalChapter> entries = new LinkedList<>();

        for (JournalManager.JournalChapter journalChapter : playerEntries.keySet()) {
            entries.add(journalChapter);
        }

        chapterList.setList(entries);
    }

    private void updateJournal() {
        JournalManager journalManager = CoreRegistry.get(JournalManager.class);
        EntityRef playerEntity = CoreRegistry.get(LocalPlayer.class).getCharacterEntity();
        Map<JournalManager.JournalChapter, DocumentData> playerEntries = journalManager.getPlayerEntries(playerEntity);

        journalList.navigateTo(playerEntries.get(selectedChapter));
    }
}
