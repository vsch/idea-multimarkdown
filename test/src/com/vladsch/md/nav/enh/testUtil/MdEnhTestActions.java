/*
 * Copyright (c) 2015-2019 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.vladsch.md.nav.enh.testUtil;

import com.vladsch.plugin.test.util.TestIdeActions;

public interface MdEnhTestActions extends TestIdeActions {
    String BalloonTest = "MarkdownNavigator.BalloonTest";
    String BlockQuoteAdd = "MarkdownNavigator.BlockQuoteAdd";
    String BlockQuoteRemove = "MarkdownNavigator.BlockQuoteRemove";
    String CopyForHardBreaks = "MarkdownNavigator.CopyForHardBreaks";
    String CopyHtmlMimeExported = "MarkdownNavigator.CopyHtmlMimeExported";
    String CopyHtmlMimeFormatted = "MarkdownNavigator.CopyHtmlMimeFormatted";
    String CopyJiraFormatted = "MarkdownNavigator.CopyJiraFormatted";
    String CopySelectionReference = "MarkdownNavigator.CopySelectionReference";
    String CopyYouTrackFormatted = "MarkdownNavigator.CopyYouTrackFormatted";
    String CyclicPreviewChange = "MarkdownNavigator.CyclicPreviewChange";
    String CyclicSplitLayoutChange = "MarkdownNavigator.CyclicSplitLayoutChange";
    String DebugBreakInjectionOnLoadAction = "MarkdownNavigator.DebugBreakInjectionOnLoadAction";
    String DebugBreakOnLoadAction = "MarkdownNavigator.DebugBreakOnLoadAction";
    String DebugPreviewAction = "MarkdownNavigator.DebugPreviewAction";
    String DebugTextBoundsToggleState = "MarkdownNavigator.DebugTextBoundsToggleState";
    String DeleteColumn = "MarkdownNavigator.DeleteColumn";
    String DeleteGeneratedFiles = "MarkdownNavigator.DeleteGeneratedFiles";
    String DeleteInvalidGeneratedFiles = "MarkdownNavigator.DeleteInvalidGeneratedFiles";
    String DeleteRow = "MarkdownNavigator.DeleteRow";
    String EditorAndPreviewLayoutChange = "MarkdownNavigator.EditorAndPreviewLayoutChange";
    String EditorOnlyLayoutChange = "MarkdownNavigator.EditorOnlyLayoutChange";
    String ExceptionTest = "MarkdownNavigator.ExceptionTest";
    String ExportAllHtml = "MarkdownNavigator.ExportAllHtml";
    String ExportAllHtmlForced = "MarkdownNavigator.ExportAllHtmlForced";
    String ExportHtml = "MarkdownNavigator.ExportHtml";
    String ExportPdf = "MarkdownNavigator.ExportPdf";
    String HeaderLevelDown = "MarkdownNavigator.HeaderLevelDown";
    String HeaderLevelUp = "MarkdownNavigator.HeaderLevelUp";
    String HeaderToggleType = "MarkdownNavigator.HeaderToggleType";
    String HtmlPreviewChange = "MarkdownNavigator.HtmlPreviewChange";
    String InsertColumn = "MarkdownNavigator.InsertColumn";
    String InsertColumnRight = "MarkdownNavigator.InsertColumnRight";
    String InsertLink = "MarkdownNavigator.InsertLink";
    String InsertRow = "MarkdownNavigator.InsertRow";
    String InsertTable = "MarkdownNavigator.InsertTable";
    String ListBulletItems = "MarkdownNavigator.ListBulletItems";
    String ListIndent = "MarkdownNavigator.ListIndent";
    String ListLoose = "MarkdownNavigator.ListLoose";
    String ListOrderedItems = "MarkdownNavigator.ListOrderedItems";
    String ListTaskItems = "MarkdownNavigator.ListTaskItems";
    String ListTight = "MarkdownNavigator.ListTight";
    String ListToggleTaskItemDone = "MarkdownNavigator.ListToggleTaskItemDone";
    String ListUnIndent = "MarkdownNavigator.ListUnIndent";
    String ModifiedHtmlTextChange = "MarkdownNavigator.ModifiedHtmlTextChange";
    String MoveColumnLeft = "MarkdownNavigator.MoveColumnLeft";
    String MoveColumnRight = "MarkdownNavigator.MoveColumnRight";
    String NextTableCell = "MarkdownNavigator.NextTableCell";
    String NextTableCellEnd = "MarkdownNavigator.NextTableCellEnd";
    String NextTableCellEndWithSelection = "MarkdownNavigator.NextTableCellEndWithSelection";
    String NextTableCellStart = "MarkdownNavigator.NextTableCellStart";
    String NextTableCellStartWithSelection = "MarkdownNavigator.NextTableCellStartWithSelection";
    String NextTableCellWithSelection = "MarkdownNavigator.NextTableCellWithSelection";
    String OpenFileTest = "MarkdownNavigator.OpenFileTest";
    String PreviewOnlyLayoutChange = "MarkdownNavigator.PreviewOnlyLayoutChange";
    String PreviousTableCell = "MarkdownNavigator.PreviousTableCell";
    String PreviousTableCellEnd = "MarkdownNavigator.PreviousTableCellEnd";
    String PreviousTableCellEndWithSelection = "MarkdownNavigator.PreviousTableCellEndWithSelection";
    String PreviousTableCellStart = "MarkdownNavigator.PreviousTableCellStart";
    String PreviousTableCellStartWithSelection = "MarkdownNavigator.PreviousTableCellStartWithSelection";
    String PreviousTableCellWithSelection = "MarkdownNavigator.PreviousTableCellWithSelection";
    String PrintPreviewAction = "MarkdownNavigator.PrintPreviewAction";
    String ReformatDocument = "MarkdownNavigator.ReformatDocument";
    String ReformatDocumentAsCommonMark = "MarkdownNavigator.ReformatDocumentAsCommonMark";
    String ReformatDocumentAsFixedIndent = "MarkdownNavigator.ReformatDocumentAsFixedIndent";
    String ReformatDocumentAsGitHubDocument = "MarkdownNavigator.ReformatDocumentAsGitHubDocument";
    String ReformatElement = "MarkdownNavigator.ReformatElement";
    String SelectFileTest = "MarkdownNavigator.SelectFileTest";
    String ShowTextHex = "MarkdownNavigator.ShowTextHex";
    String SizePreferencesDialog = "MarkdownNavigator.SizePreferencesDialog";
    String TableSort = "MarkdownNavigator.TableSort";
    String TableAsJson = "MarkdownNavigator.TableAsJson";
    String ToggleAutoFormatTable = "MarkdownNavigator.ToggleAutoFormatTable";
    String ToggleBold = "MarkdownNavigator.ToggleBold";
    String ToggleCodeSpan = "MarkdownNavigator.ToggleCodeSpan";
    String ToggleEditorSplitLayout = "MarkdownNavigator.ToggleEditorSplitLayout";
    String ToggleItalic = "MarkdownNavigator.ToggleItalic";
    String ToggleSplitLayoutChange = "MarkdownNavigator.ToggleSplitLayoutChange";
    String ToggleStrikeThrough = "MarkdownNavigator.ToggleStrikeThrough";
    String ToggleSubscript = "MarkdownNavigator.ToggleSubscript";
    String ToggleSuperscript = "MarkdownNavigator.ToggleSuperscript";
    String ToggleSuspendLicense = "MarkdownNavigator.ToggleSuspendLicense";
    String ToggleUnderline = "MarkdownNavigator.ToggleUnderline";
    String ToggleUseActualWidth = "MarkdownNavigator.ToggleUseActualWidth";
    String ToggleWrapOnTyping = "MarkdownNavigator.ToggleWrapOnTyping";
    String TranslateDocument = "MarkdownNavigator.TranslateDocument";
    String TableTranspose = "MarkdownNavigator.TableTranspose";
    String UnmodifiedHtmlTextChange = "MarkdownNavigator.UnmodifiedHtmlTextChange";
}
