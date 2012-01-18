(function() {
	/**
	* The accordionview module provides a widget for managing content in an accordion.
	* @module accordionview
	* @requires yahoo, dom, event, element, animation
	*
	* By Marco van Hylckama Vlieg (marco@i-marco.nl)
	* Some inspiration taken from the YUI TabView widget
	*
	* THIS IS A WORK IN PROGRESS
	*
	*/
	
	/**
	* A widget to control accordion views.
	* @namespace YAHOO.widget
	* @class AccordionView
	* @extends YAHOO.util.Element
	* @constructor
	* @param {HTMLElement | String} The id of the html 
	* element that represents the AccordionView. 
	* @param {Object} oAttr (optional) A key map of the AccordionView's 
	* initial oAttributes.  
	*/


	var YUD = YAHOO.util.Dom, YUE = YAHOO.util.Event, YUA = YAHOO.util.Anim, YUS = YAHOO.util.Selector;

	var AccordionView = function(el, oAttr) {
		
		el = YUD.get(el);
		
		// some sensible defaults
		
		oAttr = oAttr || {};

		oAttr.collapsible = oAttr.collapsible || true;
		oAttr.animate = oAttr.animate || true;
		oAttr.animatespeed = oAttr.animatespeed || 0.7;
		oAttr.expandable = oAttr.expandable || false;
		oAttr.effect = oAttr.effect || YAHOO.util.Easing.easeBoth;
		oAttr.width = oAttr.width || '400px';
		

		if(!YUD.get(el)) {
			//assume we only got oAttributes

			return false;
			//el = _createAccordionViewElement.call(this, el, oAttr);
		}
		YAHOO.widget.AccordionView.superclass.constructor.call(this, el, oAttr); 
		this.initList(el, oAttr);
		return true;
	};



	YAHOO.widget.AccordionView = AccordionView;

	YAHOO.extend(AccordionView, YAHOO.util.Element, {
		
		// attributeprovider stuff
		
		initAttributes: function (oAttr) {
			AccordionView.superclass.initAttributes.call(this, oAttr);
			
			this.setAttributeConfig('id', {
		        writeOnce: true,
		        validator: function (value) {
		            return (/^[a-zA-Z_][\w]*$/.test(value));
		        },
		        value: YUD.generateId(),
		        method: function (value) {
		            this.id = value;
		        }
		    });
			this.setAttributeConfig('width', {
				value: oAttr.width,
				method: function (value) {
					this.setStyle('width', value);
					}
				}
			);

			this.setAttributeConfig('animationSpeed', {
				value: oAttr.animationSpeed
				}
			);
			this.setAttributeConfig('animate', {
				value: oAttr.animate,
				validator: YAHOO.lang.isBoolean
				}
			);			
			this.setAttributeConfig('collapsible', {
				value: oAttr.collapsible,
				validator: YAHOO.lang.isBoolean
				}
			);
			this.setAttributeConfig('expandable', {
				value: oAttr.expandable,
				validator: YAHOO.lang.isBoolean
				}
			);
			this.setAttributeConfig('effect', {
				value: oAttr.effect,
				validator: YAHOO.lang.isString

				}
			);
			this.setAttributeConfig('hoverActivated', {
			        value: false,
			        validator: YAHOO.lang.isBoolean,
			        method: function (value) {
			                if (value) {
			                        YUE.on(this, 'mouseover', this.handleClicks, this, true);                        
			                } else {
			                        YUE.removeListener(this, 'mouseover', this.handleClicks);
			                }        
			        }
			});
			
		}
	});
			
		
	var proto = AccordionView.prototype;
	
	/**
	* The className to add when building from scratch. 
	* NOT used yet
	* @property CLASSNAME
	* @default "yui-accordionview"
	*/
	proto.CLASSNAME = 'yui-accordionview';
		
	proto.initList = function(el, oAttr) {	
		
		var aItems = this.collapseAccordion();
		if((oAttr.expandItem === 0) || (oAttr.expandItem > 1)) {			
			YUD.removeClass(aItems[oAttr.expandItem], 'hidden');			
			YUD.addClass(YUD.getElementsByClassName('yui-accordion-toggle', 'a', this)[oAttr.expandItem], 'active');
		}
		if(true === oAttr.hoverActivated) {
			YUE.on(el, 'mouseover', this.handleClicks, this, true);			
			YUE.on(el, 'click', this.handleClicks, this, true);
		}
		else {
			YUE.on(el, 'click', this.handleClicks, this, true);
		}		
	};

	proto.collapseAccordion = function() {
		var aItems = YUD.getElementsByClassName('yui-accordion-content' ,'div', this);
		YUD.batch(aItems, function(e) {
			if(YUD.getElementsByClassName('indicator', 'span', e.parentNode).length === 0) {
				var el = document.createElement('span');
				YUD.addClass(el, 'indicator');
				e.parentNode.firstChild.appendChild(el);
			}
			if(!YUD.hasClass(this.parentNode, 'yui-accordion-content')) { 
				YUD.removeClass(e.parentNode.firstChild, 'active');
				YUD.addClass(e, 'hidden');
			}
		}, this);
		return aItems;
	};

	/**
	* Adds an Accordion panel to the AccordionView instance.  
	* If no index is specified, the panel is added to the end of the tab list.
	* @method addPanel
	* @param {Object} oAttr A key map of the Panel's properties
	* @param {Integer} nIndex The position to add the tab. 
	* @return void
	*/

	proto.addPanel = function(oAttr, nIndex) {
		var oPanelParent = document.createElement('li');
		YUD.addClass(oPanelParent, 'yui-accordion-panel');
		var elPanelLink = document.createElement('a');
		var elIndicator = document.createElement('span');
		YUD.addClass(elIndicator, 'indicator');
		elPanelLink.innerHTML = oAttr.label || '';
		elPanelLink.appendChild(elIndicator);		
		YUD.addClass(elPanelLink, 'yui-accordion-toggle');
		elPanelLink.href = oAttr.href || '#';
		oPanelParent.appendChild(elPanelLink);
		var elPanelContent = document.createElement('div');
		elPanelContent.innerHTML = oAttr.content || '';
		YUD.addClass(elPanelContent, 'yui-accordion-content');
		oPanelParent.appendChild(elPanelContent);
		
		if((nIndex !== null) && (nIndex !== undefined)) {
			var panelBefore = this.getPanel(nIndex);
			this.insertBefore(oPanelParent, panelBefore);
		}
		else {
			this.appendChild(oPanelParent);
		}

		if(oAttr.expand) {
			this.collapseAccordion();
			YUD.removeClass(elPanelContent, 'hidden');
			YUD.addClass(elPanelLink, 'active');
		}
		else {
			YUD.addClass(elPanelContent, 'hidden');
		}
	};

	/**
	* Removes the specified Panel from the AccordionView.
	* @method removePanel
	* @param {Integer} index of the panel to be removed
	* @return void
	*/

	proto.removePanel = function(index) {
		this.removeChild(YUD.getElementsByClassName('yui-accordion-panel', 'li', this)[index]);		
	};

	/**
	* Returns the HTMLElement of the panel at the specified index.
	* @method getPanel
	* @param {Integer} nIndex The position of the Panel.
	* @return HTMLElement
	*/

	proto.getPanel = function(nIndex) {
		var aPanels = YUD.getElementsByClassName('yui-accordion-panel', 'li', this);
		return aPanels[nIndex];
	};

	/**
	* Event handler for the AccordionView
	* We only need to handle clicks for an AccordionView
	* @method handleClicks
	* @param {event} ev The Dom event that is being handled.
	* @param {Object} oAttr The oAttributes for this AccordionView
	* @return void
	*/

	proto.handleClicks = function(ev) {
		YUE.stopPropagation(ev);			

		var elClickedNode = YUE.getTarget(ev);
		if(!YUD.hasClass(elClickedNode, 'indicator') && !YUD.hasClass(elClickedNode, 'yui-accordion-toggle') && !YUD.hasClass(elClickedNode, 'yui-accordion-panel')) {
			return true;	
		}

		function iezoom(el, sZoom) {
			if(YAHOO.env.ua.ie < 7 && YAHOO.env.ua.ie > 0) {
				var aInnerAccordions = YUD.getElementsByClassName('yui-accordionview', 'ul', el);
					if(aInnerAccordions[0]) {
						YUD.setStyle(aInnerAccordions[0], 'zoom', sZoom);
					}
				}
			}
		function iehide(el, sHide) {
			if(YAHOO.env.ua.ie < 7 && YAHOO.env.ua.ie > 0) {
				var aInnerAccordions = YUD.getElementsByClassName('yui-accordionview', 'ul', el);
					if(aInnerAccordions[0]) {
						YUD.setStyle(aInnerAccordions[0], 'visibility', sHide);
					}
				}
		}

		function toggleItem(el, elClicked) {			
			if(!elClicked) {
				if(!el) { return false ;}
				elClicked = el.parentNode.firstChild;
			}
			var oOptions = {};
			var bHideAfter = false;
			var nHeight = 0;
			if(YUD.hasClass(el, 'hidden')) {

				// still a bit experimental. trying to eliminate mild flash sometimes seen in FF //

				YUD.setStyle(el, 'display', 'block');
				YUD.addClass(el, 'almosthidden');
				YUD.removeClass(el, 'hidden');
				nHeight = el.offsetHeight;
				YUD.setStyle(el, 'height', 0);
				YUD.removeClass(el, 'almosthidden');
				oOptions = {height: {from: 0, to: nHeight}};
				YUD.removeClass(el, 'hidden');
			}
			else {
				nHeight = el.offsetHeight;
				oOptions = {height: {from: nHeight, to: 0}};
				bHideAfter = true;
			}
			if(this.get('animate')) {
				var nSpeed = (this.get('animationSpeed')) ? this.get('animationSpeed') : 0.5;
				var sEffect = (this.get('effect')) ? this.get('effect') : YAHOO.util.Easing.easeBoth;
				var oAnimator = new YUA(el, oOptions, nSpeed, sEffect);
				if(bHideAfter) {
					YUD.removeClass(elClicked, 'active');
					iehide(el, 'hidden');
					oAnimator.onComplete.subscribe(function(){
						YUD.addClass(el, 'hidden');
						YUD.setStyle(el, 'height', 'auto');
						YUD.setStyle(el, 'display', 'none');
						iezoom(el, 'normal');
					});
				}
				else {
					//changed from visible to hidden so it doesn't show up behind the parent accordion until after the animation
					iehide(el, 'hidden');
					oAnimator.onComplete.subscribe(function(){
						YUD.setStyle(el, 'height', 'auto');
						iezoom(el, '1');
						//Added to make the inner accordion visible again
						iehide(el, 'visible');
					});					
					YUD.addClass(elClicked, 'active');
				}
				oAnimator.animate();
			}
			else {
				if(bHideAfter) {
					YUD.addClass(el, 'hidden');
					YUD.setStyle(el, 'height', 'auto');
					YUD.setStyle(el, 'display', 'none');
					YUD.removeClass(elClicked, 'active');
				}
				else {
					YUD.removeClass(el, 'hidden');
					YUD.setStyle(el, 'height', 'auto');
					YUD.addClass(elClicked, 'active');
				}
			}
			return true;
		}
		
		var eTargetListNode = (elClickedNode.nodeName == 'SPAN') ? elClickedNode.parentNode.parentNode : elClickedNode.parentNode;
		
		var containedPanel = YUD.getElementsByClassName('yui-accordion-content', 'div', eTargetListNode)[0]; 

		if(this.get('collapsible') === false) {
			if (!YUD.hasClass(containedPanel, 'hidden')) {
				YUE.preventDefault(ev);
				return false;
			}
		}
		else {
			if(!YUD.hasClass(containedPanel, 'hidden')) {
				toggleItem.call(this, containedPanel);
				YUE.preventDefault(ev);
				return false;				
			}
		}

		if(this.get('expandable') !== true) {
			var aPanelCollection = YUD.getElementsByClassName('yui-accordion-content', 'div', this);

			// skip panels within a panel

			for(var element in aPanelCollection) {
				if(this.get('element').id == aPanelCollection[element].parentNode.parentNode.id) {
					var bMustToggle = YUD.hasClass(aPanelCollection[element], 'hidden');
					if(!bMustToggle) {
						toggleItem.call(this,aPanelCollection[element]);
					}
				}
			}
		}
		if(elClickedNode.nodeName == 'SPAN')  {
			toggleItem.call(this, containedPanel, elClickedNode.parentNode);
		}
		else {
			toggleItem.call(this, containedPanel, elClickedNode);
		}
		
		YUE.preventDefault(ev);
		return true;
	};

	var _createAccordionViewElement = function(oAttr) {
		// dummy for now
	};

	/**
	* Provides a readable name for the AccordionView instance.
	* @method toString
	* @return String
	*/
	proto.toString = function() {
		var name = this.get('id') || this.get('tagName');
		return "AccordionView " + name; 
	};
})();
YAHOO.register("accordionview", YAHOO.widget.AccordionView, {version: "0.99", build: "9"});