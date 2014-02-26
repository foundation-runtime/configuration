package com.cisco.oss.foundation.configuration.xml;

import com.cisco.oss.foundation.configuration.xml.jaxb.HierarchyNode;
import com.cisco.oss.foundation.configuration.xml.jaxb.HierarchyTree;
import junit.framework.TestCase;

import java.util.List;

public class HierarchyTreeMessageTest extends TestCase {

    public void testUnmarshall_Simple() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.HIERARCHY_TREE);

        new HierarchyTreeMessage(xml);
    }
    public void testUnmarshall_ErrorHandling_WrongMessageType() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.NAMESPACE_DEFINITIONS);

        try {
            new HierarchyTreeMessage(xml);
            fail("XmlException expected");
        } catch(XmlException e) {
            // good.  We expect a XmlException since this message doesn't correspond to a HierarchyTree Message.
        }
    }
    public void testUnmarshall_MemberCheck() throws Exception {
        String xml = XmlTestUtil.getXml(XmlTestUtil.XML_FILES.HIERARCHY_TREE);

        HierarchyTreeMessage msg = new HierarchyTreeMessage(xml);

        HierarchyTree jaxb = msg.jaxb();

        HierarchyNode global = jaxb.getHierarchyNode();
        assertEquals("global.getId() = " + global.getId(), global.getId(), "0");
        assertEquals("global.getName() = " + global.getName(), global.getName(), "UHE");
        assertEquals("global.getLevelName() = " + global.getLevelName(), global.getLevelName().toString(), "GLOBAL");
        assertEquals("global.getLevelOrder() = " + global.getLevelOrder(), global.getLevelOrder().intValue(), 999);
        assertEquals("global.getParentId() = " + global.getParentId(), global.getParentId(), null);
        assertEquals("global.getProcessName() = " + global.getProcessName(), global.getProcessName(), null);

        List<HierarchyNode> groups = global.getHierarchyNodes();
        assertEquals("groups.size() = " + groups.size(), groups.size(), 2);

        HierarchyNode node1 = groups.get(0);
        HierarchyNode node2 = groups.get(1);

        assertEquals("node1.getId() = " + node1.getId(), node1.getId(), "1");
        assertEquals("node1.getName() = " + node1.getName(), node1.getName(), "ProdX-East");
        assertEquals("node1.getLevelName() = " + node1.getLevelName(), node1.getLevelName().toString(), "GROUP");
        assertEquals("node1.getLevelOrder() = " + node1.getLevelOrder(), node1.getLevelOrder().intValue(), 500);
        assertEquals("node1.getParentId() = " + node1.getParentId(), node1.getParentId(), null); // inferred from XML - but not actually set
        assertEquals("node1.getProcessName() = " + node1.getProcessName(), node1.getProcessName(), null);

        assertEquals("node2.getId() = " + node2.getId(), node2.getId(), "2");
        assertEquals("node2.getName() = " + node2.getName(), node2.getName(), "ProdX-West");
        assertEquals("node2.getLevelName() = " + node2.getLevelName(), node2.getLevelName().toString(), "GROUP");
        assertEquals("node2.getLevelOrder() = " + node2.getLevelOrder(), node2.getLevelOrder().intValue(), 500);
        assertEquals("node2.getParentId() = " + node2.getParentId(), node2.getParentId(), null); // inferred from XML - but not actually set
        assertEquals("node2.getProcessName() = " + node2.getProcessName(), node2.getProcessName(), null);

        groups = node1.getHierarchyNodes();
        assertEquals("groups.size() = " + groups.size(), groups.size(), 4);
        
        HierarchyNode node6 = groups.get(0);
        HierarchyNode node7 = groups.get(1);
        HierarchyNode node8 = groups.get(2);
        HierarchyNode node9 = groups.get(3);

        assertEquals("node6.getId() = " + node6.getId(), node6.getId(), "6");
        assertEquals("node6.getName() = " + node6.getName(), node6.getName(), "ProdX-East-1");
        assertEquals("node6.getLevelName() = " + node6.getLevelName(), node6.getLevelName().toString(), "COMPONENT");
        assertEquals("node6.getLevelOrder() = " + node6.getLevelOrder(), node6.getLevelOrder().intValue(), 0);
        assertEquals("node6.getParentId() = " + node6.getParentId(), node6.getParentId(), null); // inferred from XML - but not actually set
        assertEquals("node6.getProcessName() = " + node6.getProcessName(), node6.getProcessName(), "dathomir.labs.nds.ca/opt/nds/uhe/ProdX/prodx");
        assertEquals("node6.getHierarchyNodes()size() = " + node6.getHierarchyNodes().size(), node6.getHierarchyNodes().size(), 0);

        assertEquals("node7.getId() = " + node7.getId(), node7.getId(), "7");
        assertEquals("node7.getName() = " + node7.getName(), node7.getName(), "ProdX-East-2");
        assertEquals("node7.getLevelName() = " + node7.getLevelName(), node7.getLevelName().toString(), "COMPONENT");
        assertEquals("node7.getLevelOrder() = " + node7.getLevelOrder(), node7.getLevelOrder().intValue(), 0);
        assertEquals("node7.getParentId() = " + node7.getParentId(), node7.getParentId(), null); // inferred from XML - but not actually set
        assertEquals("node7.getProcessName() = " + node7.getProcessName(), node7.getProcessName(), "yavin4.labs.nds.ca/opt/nds/uhe/ProdX/prodx");
        assertEquals("node7.getHierarchyNodes()size() = " + node7.getHierarchyNodes().size(), node7.getHierarchyNodes().size(), 0);

        assertEquals("node8.getId() = " + node8.getId(), node8.getId(), "8");
        assertEquals("node8.getName() = " + node8.getName(), node8.getName(), "ProdX-East-3");
        assertEquals("node8.getLevelName() = " + node8.getLevelName(), node8.getLevelName().toString(), "COMPONENT");
        assertEquals("node8.getLevelOrder() = " + node8.getLevelOrder(), node8.getLevelOrder().intValue(), 0);
        assertEquals("node8.getParentId() = " + node8.getParentId(), node8.getParentId(), null); // inferred from XML - but not actually set
        assertEquals("node8.getProcessName() = " + node8.getProcessName(), node8.getProcessName(), "dantooine.labs.nds.ca/opt/nds/uhe/ProdX/prodx");
        assertEquals("node8.getHierarchyNodes()size() = " + node8.getHierarchyNodes().size(), node8.getHierarchyNodes().size(), 0);

        assertEquals("node9.getId() = " + node9.getId(), node9.getId(), "9");
        assertEquals("node9.getName() = " + node9.getName(), node9.getName(), "ProdX-East-4");
        assertEquals("node9.getLevelName() = " + node9.getLevelName(), node9.getLevelName().toString(), "COMPONENT");
        assertEquals("node9.getLevelOrder() = " + node9.getLevelOrder(), node9.getLevelOrder().intValue(), 0);
        assertEquals("node9.getParentId() = " + node9.getParentId(), node9.getParentId(), null); // inferred from XML - but not actually set
        assertEquals("node9.getProcessName() = " + node9.getProcessName(), node9.getProcessName(), "nemoidia.labs.nds.ca/opt/nds/uhe/ProdX/prodx");
        assertEquals("node9.getHierarchyNodes()size() = " + node9.getHierarchyNodes().size(), node9.getHierarchyNodes().size(), 0);

        groups = node2.getHierarchyNodes();
        assertEquals("groups.size() = " + groups.size(), groups.size(), 2);
        
        HierarchyNode node10 = groups.get(0);
        HierarchyNode node11 = groups.get(1);

        assertEquals("node10.getId() = " + node10.getId(), node10.getId(), "10");
        assertEquals("node10.getName() = " + node10.getName(), node10.getName(), "ProdX-West-1");
        assertEquals("node10.getLevelName() = " + node10.getLevelName(), node10.getLevelName().toString(), "COMPONENT");
        assertEquals("node10.getLevelOrder() = " + node10.getLevelOrder(), node10.getLevelOrder().intValue(), 0);
        assertEquals("node10.getParentId() = " + node10.getParentId(), node10.getParentId(), null); // inferred from XML - but not actually set
        assertEquals("node10.getProcessName() = " + node10.getProcessName(), node10.getProcessName(), "kirk.labs.nds.ca/opt/nds/uhe/ProdX/prodx");
        assertEquals("node10.getHierarchyNodes()size() = " + node10.getHierarchyNodes().size(), node10.getHierarchyNodes().size(), 0);

        assertEquals("node11.getId() = " + node11.getId(), node11.getId(), "11");
        assertEquals("node11.getName() = " + node11.getName(), node11.getName(), "ProdX-West-2");
        assertEquals("node11.getLevelName() = " + node11.getLevelName(), node11.getLevelName().toString(), "COMPONENT");
        assertEquals("node11.getLevelOrder() = " + node11.getLevelOrder(), node11.getLevelOrder().intValue(), 0);
        assertEquals("node11.getParentId() = " + node11.getParentId(), node11.getParentId(), null); // inferred from XML - but not actually set
        assertEquals("node11.getProcessName() = " + node11.getProcessName(), node11.getProcessName(), "bones.labs.nds.ca/opt/nds/uhe/ProdX/prodx");
        assertEquals("node11.getHierarchyNodes()size() = " + node11.getHierarchyNodes().size(), node11.getHierarchyNodes().size(), 0);
    }

    public void testMarshall_Simple() throws Exception {
        HierarchyTree jaxb = (HierarchyTree)XmlTestUtil.getJaxb(XmlTestUtil.XML_FILES.HIERARCHY_TREE);

        new HierarchyTreeMessage(jaxb);
    }
}
