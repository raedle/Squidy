/**
 * Squidy Interaction Library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Squidy Interaction Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Squidy Interaction Library. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * 2009 Human-Computer Interaction Group, University of Konstanz.
 * <http://hci.uni-konstanz.de>
 * 
 * Please contact info@squidy-lib.de or visit our website
 * <http://www.squidy-lib.de> for further information.
 */

@javax.xml.bind.annotation.XmlSchema(
        xmlns = {
        		@XmlNs(prefix = "xsi", namespaceURI = "http://www.w3.org/2001/XMLSchema-instance"),
        		@XmlNs(prefix = "common", namespaceURI = Namespaces.NAMESPACE_PREFIX_COMMON),
        		@XmlNs(prefix = "manager", namespaceURI = Namespaces.NAMESPACE_PREFIX_MANAGER)
        		},
        		namespace = Namespaces.NAMESPACE_PREFIX_DESIGNER
)
@javax.xml.bind.annotation.XmlAccessorType(
        javax.xml.bind.annotation.XmlAccessType.NONE
) package org.squidy.designer.zoom;

import javax.xml.bind.annotation.XmlNs;

import org.squidy.Namespaces;



