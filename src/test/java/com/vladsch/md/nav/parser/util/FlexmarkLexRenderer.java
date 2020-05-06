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

package com.vladsch.md.nav.parser.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.test.util.spec.IRenderBase;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.ScopedDataSet;
import com.vladsch.flexmark.util.misc.Ref;
import com.vladsch.md.nav.testUtil.MdEnhSpecTestSetup;
import com.vladsch.md.nav.parser.PegdownOptionsAdapter;
import com.vladsch.md.nav.parser.api.HtmlPurpose;
import com.vladsch.md.nav.parser.api.ParserPurpose;
import com.vladsch.md.nav.settings.MdRenderingProfile;
import com.vladsch.md.nav.vcs.MdLinkResolver;
import org.jetbrains.annotations.NotNull;

public class FlexmarkLexRenderer extends IRenderBase {
    final static public DataKey<ParserPurpose> PARSER_PURPOSE = new DataKey<>("PARSER_PURPOSE", ParserPurpose.HTML);
    final static public DataKey<HtmlPurpose> HTML_PURPOSE = new DataKey<>("HTML_PURPOSE", HtmlPurpose.RENDER);
    final static public DataKey<Ref<MdLinkResolver>> LINK_RESOLVER_REF = new DataKey<>("LINK_RESOLVER_REF", new Ref<>(null));

    public FlexmarkLexRenderer(DataHolder options) {
        super(combine(options, new PegdownOptionsAdapter()
                .getFlexmarkOptions(PARSER_PURPOSE.get(options),
                        HTML_PURPOSE.get(options),
                        LINK_RESOLVER_REF.get(options).value,
                        MdEnhSpecTestSetup.RENDERING_PROFILE_OPTION.setInstanceData(new MdRenderingProfile(), options)
                )));
    }

    private static DataHolder combine(DataHolder options, DataHolder overrides) {
        ScopedDataSet set = new ScopedDataSet(options, overrides);
        return set;
    }

    @Override
    public void render(final Node node, @NotNull final Appendable output) {
        DataHolder options = getOptions();
        Parser parser = Parser.builder(options).build();
        Document document = parser.parse(node.getChars());
        HtmlRenderer.builder(options).build().render(document, output);
    }
}
